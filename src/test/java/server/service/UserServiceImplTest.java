package server.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import server.domain.User;
import server.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class UserServiceImplTest {
    private final EncryptService encryptService = mock(EncryptService.class);
    private final UserRepository repository = mock(UserRepository.class);
    private final UserServiceImpl userService = new UserServiceImpl(encryptService, repository);

    @ParameterizedTest
    @CsvSource({
            "a,",
            ", asfa",
            "'',24124",
            "asdfg3,''"
    })
    void invalidInputs(String name, String pw) {
        assertThatThrownBy(() -> userService.createUser(name, pw)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void userAlreadyExists() {
        when(repository.findByName(anyString())).thenReturn(new User());
        assertThatThrownBy(() -> userService.createUser("myname", "mypass")).hasMessageContaining("exist");
    }

    @Test
    void saveUser() {
        byte[] encryptedPw = new byte[]{1,2,3};
        when(encryptService.encrypt(anyString(), any())).thenReturn(encryptedPw);

        User user = userService.createUser("myname", "mypass");
        verify(encryptService).encrypt(anyString(), any());
        verify(repository).findByName("myname");
        verify(repository).upsert(any());

        assertThat(user.getName()).isEqualTo("myname");
        assertThat(user.getPw()).isEqualTo(encryptedPw);
        assertThat(user.getPwSaltStrategy()).isNotNull();
    }

    @Test
    void deleteUser() {
        User user = new User();
        userService.deleteUser(user);
        verify(repository).delete(user);
    }
}
