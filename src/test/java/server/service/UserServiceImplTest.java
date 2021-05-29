package server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import server.domain.Role;
import server.domain.User;
import server.repository.RoleRepository;
import server.repository.UserRepository;
import server.service.crypto.EncryptService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class UserServiceImplTest {
    private final EncryptService encryptService = mock(EncryptService.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final RoleRepository roleRepository = mock(RoleRepository.class);
    private final UserService service = new UserServiceImpl(encryptService, userRepository, roleRepository);

    @BeforeEach
    void reset() {
        Mockito.reset(encryptService, userRepository, roleRepository); // slightly faster than creating mock instance for each test
    }

    @Test
    void createUser() {
        byte[] encryptedPw = new byte[]{1, 2, 3};
        when(encryptService.encrypt(anyString(), any())).thenReturn(encryptedPw);

        User user = service.createUser("myname", "mypass");
        verify(encryptService).encrypt(anyString(), any());
        verify(userRepository).upsert(any());

        assertThat(user.getName()).isEqualTo("myname");
        assertThat(user.getPw()).isEqualTo(encryptedPw);
        assertThat(user.getPwSaltStrategy()).isNotNull();
    }

    @Test
    void deleteUser() {
        User user = User.builder().build();
        service.deleteUser(user);
        verify(userRepository).delete(user);
    }

    @Test
    void createRole() {
        Role role = service.createRole("role2");
        verify(roleRepository).upsert(role);

        assertThat(role.getName()).isEqualTo("role2");
    }

    @Test
    void deleteRole() {
        Role role = new Role("role3");
        service.deleteRole(role);
        verify(roleRepository).delete(role);
    }

    @Test
    void addRoleToUser() {
        Role role = new Role("r1");
        User user = User.builder().build();
        when(roleRepository.findByName("r1")).thenReturn(role);

        service.addRoleToUser(user, role);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).upsert(userCaptor.capture());
        assertThat(userCaptor.getValue().getRoles()).contains(role);
    }
}
