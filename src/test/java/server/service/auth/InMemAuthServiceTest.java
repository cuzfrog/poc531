package server.service.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.util.collections.Sets;
import server.domain.Role;
import server.domain.User;
import server.repository.UserRepository;
import server.service.crypto.EncryptService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class InMemAuthServiceTest {
    private static final long TOKEN_DURATION_MS = 10;
    private final TokenRepository tokenRepository = mock(TokenRepository.class);
    private final EncryptService encryptService = mock(EncryptService.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final TimeService timeService = mock(TimeService.class);
    private final AuthService authService = new InMemAuthService(tokenRepository, encryptService, userRepository, timeService, TOKEN_DURATION_MS);
    private final ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);

    private final Instant aFutureTime = Instant.now().plus(5, ChronoUnit.HOURS);

    @BeforeEach
    void reset() {
        Mockito.reset(tokenRepository, encryptService, userRepository, timeService);
    }

    @Test
    void authenticateWhenInvalidInputs() {
        // similar to some existing ones
    }

    @Test
    void authenticateWhenNoUser() {
        assertThatThrownBy(() -> authService.authenticate("user1", "pw3")).hasMessageContaining("not exist");
    }

    @Test
    void authenticateWhenWrongPw() {
        User existingUser = new User();
        existingUser.setPw(new byte[]{1, 5});
        when(userRepository.findByName("user1")).thenReturn(existingUser);
        when(encryptService.encrypt(eq("pw3"), any())).thenReturn(new byte[]{1, 8});
        assertThatThrownBy(() -> authService.authenticate("user1", "pw3")).hasMessageContaining("failed");
    }

    @Test
    void authenticate() {
        User existingUser = new User();
        existingUser.setPw(new byte[]{1, 5});
        existingUser.addRole(new Role("r3232"));
        when(userRepository.findByName("user2")).thenReturn(existingUser);
        when(encryptService.encrypt(eq("pw3"), any())).thenReturn(new byte[]{1, 5});
        Instant creationTime = Instant.now();
        when(timeService.now()).thenReturn(creationTime);

        String tokenId = authService.authenticate("user2", "pw3");

        verify(tokenRepository).save(tokenCaptor.capture());
        Token captured = tokenCaptor.getValue();
        assertThat(tokenId).isEqualTo(captured.getId());
        assertThat(captured.getRoles()).isEqualTo(existingUser.getRoles());
        assertThat(captured.getExpirationTime()).isEqualTo(creationTime.plus(TOKEN_DURATION_MS, ChronoUnit.MILLIS));
    }

    @Test
    void invalidateToken() {
        authService.invalidateToken("abc");
        verify(tokenRepository).remove("abc");
    }

    @Test
    void authorize() {
        Role role = new Role("admin");
        when(tokenRepository.findById("abc")).thenReturn(new Token("abc", aFutureTime, Sets.newSet(role)));
        when(timeService.now()).thenReturn(aFutureTime.minus(1, ChronoUnit.HOURS));
        assertThat(authService.authorize("abc", role)).isTrue();
    }

    @Test
    void authorizeFailWhenNoSuchRole() {
        Role role = new Role("admin");
        when(tokenRepository.findById("abc")).thenReturn(new Token("abc", aFutureTime, Sets.newSet(new Role("super"))));
        when(timeService.now()).thenReturn(aFutureTime.minus(1, ChronoUnit.HOURS));
        assertThat(authService.authorize("abc", role)).isFalse();
    }

    @Test
    void authorizeFailWhenNoSuchToken() {
        when(timeService.now()).thenReturn(aFutureTime.minus(1, ChronoUnit.HOURS));
        assertThatThrownBy(() -> authService.authorize("abc", new Role("r3"))).hasMessageContaining("Invalid");
        assertThatThrownBy(() -> authService.allRoles("abc")).hasMessageContaining("Invalid");

    }

    @Test
    void authorizeFailWhenTokenExpires() {
        Role role = new Role("admin");
        when(tokenRepository.findById("abc")).thenReturn(new Token("abc", aFutureTime, Sets.newSet(role)));
        when(timeService.now()).thenReturn(aFutureTime.plus(1, ChronoUnit.HOURS));
        assertThatThrownBy(() -> authService.authorize("abc", role)).hasMessageContaining("expired");
        assertThatThrownBy(() -> authService.allRoles("abc")).hasMessageContaining("expired");

    }

    @Test
    void getAllRoles() {
        Role r1 = new Role("user");
        Role r2 = new Role("admin");
        when(tokenRepository.findById("abc")).thenReturn(new Token("abc", aFutureTime, Sets.newSet(r1 ,r2)));
        when(timeService.now()).thenReturn(aFutureTime.minus(1, ChronoUnit.HOURS));

        assertThat(authService.allRoles("abc")).containsExactlyInAnyOrder(r1, r2);
    }
}
