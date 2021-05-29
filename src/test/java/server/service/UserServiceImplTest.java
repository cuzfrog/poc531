package server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import server.domain.Role;
import server.domain.User;
import server.repository.RoleRepository;
import server.repository.UserRepository;
import server.service.crypto.EncryptService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    private final UserServiceImpl userService = new UserServiceImpl(encryptService, userRepository, roleRepository);

    @BeforeEach
    void reset() {
        Mockito.reset(encryptService, userRepository, roleRepository); // slightly faster than creating mock instance for each test
    }

    @ParameterizedTest
    @CsvSource({
            "a,",
            ", asfa",
            "'',24124",
            "asdfg3,''"
    })
    void createUserWithInvalidInputs(String name, String pw) {
        assertThatThrownBy(() -> userService.createUser(name, pw)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createUserWhenAlreadyExists() {
        when(userRepository.findByName(anyString())).thenReturn(new User());
        assertThatThrownBy(() -> userService.createUser("myname", "mypass")).hasMessageContaining("exist");
    }

    @Test
    void createUser() {
        byte[] encryptedPw = new byte[]{1, 2, 3};
        when(encryptService.encrypt(anyString(), any())).thenReturn(encryptedPw);

        User user = userService.createUser("myname", "mypass");
        verify(encryptService).encrypt(anyString(), any());
        verify(userRepository).findByName("myname");
        verify(userRepository).upsert(any());

        assertThat(user.getName()).isEqualTo("myname");
        assertThat(user.getPw()).isEqualTo(encryptedPw);
        assertThat(user.getPwSaltStrategy()).isNotNull();
    }

    @Test
    void deleteUser() {
        User user = new User();
        userService.deleteUser(user);
        verify(userRepository).delete(user);
    }

    @Test
    void createRoleWithInvalidInputs() {
        assertThatThrownBy(() -> userService.createRole(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> userService.createRole("")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createRoleWhenAlreadyExists() {
        when(roleRepository.findByName("role1")).thenReturn(new Role("role1"));
        assertThatThrownBy(() -> userService.createRole("role1")).hasMessageContaining("exist");
    }

    @Test
    void createRole() {
        Role role = userService.createRole("role2");
        verify(roleRepository).findByName("role2");
        verify(roleRepository).upsert(role);

        assertThat(role.getName()).isEqualTo("role2");
    }

    @Test
    void deleteRole() {
        Role role = new Role("role3");
        userService.deleteRole(role);
        verify(roleRepository).delete(role);
    }

    @Test
    void addRoleToUser() {
        Role role = new Role("r1");
        User user = new User();
        userService.addRoleToUser(user, role);
        verify(userRepository).upsert(user);
        assertThat(user.getRoles()).contains(role);
    }
}
