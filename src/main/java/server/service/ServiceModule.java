package server.service;

import server.service.auth.AuthService;
import server.service.user.UserService;

public final class ServiceModule {
    public static AuthService authService() {
        return AuthService.getInstance();
    }

    public static UserService userService() {
        return UserService.getInstance();
    }
}
