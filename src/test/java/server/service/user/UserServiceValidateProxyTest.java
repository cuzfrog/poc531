package server.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import server.domain.Role;
import server.domain.User;
import server.repository.RoleRepository;
import server.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class UserServiceValidateProxyTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final RoleRepository roleRepository = mock(RoleRepository.class);
    private final UserService delegate = mock(UserService.class);
    private final UserService proxy = new UserServiceValidateProxy(userRepository, roleRepository, delegate);

    @ParameterizedTest
    @CsvSource({
            "a,",
            ", asfa",
            "'',24124",
            "asdfg3,''",
            "anonymous,35asd"
    })
    void createUserWithInvalidInputs(String name, String pw) {
        assertThatThrownBy(() -> proxy.createUser(name, pw)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createUserWhenAlreadyExists() {
        when(userRepository.findByName(anyString())).thenReturn(User.builder().build());
        assertThatThrownBy(() -> proxy.createUser("myname", "mypass")).hasMessageContaining("exist");
    }

    @Test
    void cannotDeleteAnonymousUser() {
        assertThatThrownBy(() -> proxy.deleteUser(User.ANONYMOUS_USER.getName())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createRoleWithInvalidInputs() {
        assertThatThrownBy(() -> proxy.createRole(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> proxy.createRole("")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createRoleWhenAlreadyExists() {
        when(roleRepository.findByName("role1")).thenReturn(new Role("role1"));
        assertThatThrownBy(() -> proxy.createRole("role1")).hasMessageContaining("exist");
    }


    @Test
    void addRoleToUserSkippedIfRoleAlreadyAssociated() {
        Role role = new Role("r1");
        User user = User.builder().withName("u3").addRole(role).build();
        when(userRepository.findByName("u3")).thenReturn(user);
        when(roleRepository.findByName("r1")).thenReturn(role);

        proxy.addRoleToUser("u3", role);
        verify(userRepository, never()).upsert(user);
    }

    @Test
    void addRoleToUserFailWhenRoleNotExists() {
        Role role = new Role("r1");
        User user = User.builder().build();
        when(userRepository.findByName("u4")).thenReturn(user);
        assertThatThrownBy(() -> proxy.addRoleToUser("u4", role)).hasMessageContaining("not exist");
    }

    @Test
    void addRoleToUserFailWhenUserNotExists() {
        Role role = new Role("r1");
        when(roleRepository.findByName("r1")).thenReturn(role);
        assertThatThrownBy(() -> proxy.addRoleToUser("uuu", role)).hasMessageContaining("not exist");
    }

    @Test
    void addRoleToUserFailForAnonymousUser() {
        Role role = new Role("r1");
        assertThatThrownBy(() -> proxy.addRoleToUser(User.ANONYMOUS_USER.getName(), role)).hasMessageContaining("Anonymous");
    }
}
