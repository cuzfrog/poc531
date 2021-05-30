package server;

import org.junit.jupiter.api.Test;
import server.domain.Role;
import server.service.ServiceModule;
import server.service.auth.AuthService;
import server.service.user.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class ExampleAcceptanceTest {
    private final UserService userService = ServiceModule.userService();
    private final AuthService authService = ServiceModule.authService();

    @Test
    void scenario1() {
        Role adminRole = userService.createRole("admin");
        Role userRole = userService.createRole("user");
        userService.createUser("admin1", "my-pw12345");
        userService.addRoleToUser("admin1", adminRole);
        userService.addRoleToUser("admin1", userRole); // API accept multiple roles can be added

        assertThatThrownBy(() -> authService.authenticate("admin1", "wrong-pass"));
        String token = authService.authenticate("admin1", "my-pw12345");

        assertThat(authService.authorize(token, userRole)).isTrue();
        assertThat(authService.authorize(token, adminRole)).isTrue();
        assertThat(authService.authorize(token, new Role("super"))).isFalse();
        assertThat(authService.allRoles(token)).containsExactlyInAnyOrder(userRole, adminRole);

        authService.invalidateToken(token);
        assertThatThrownBy(() -> authService.authorize(token, userRole));
    }
}
