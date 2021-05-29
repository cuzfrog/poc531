package server.service;

import server.service.auth.AuthService;
import server.service.user.UserService;

public final class ServiceModule {
    public static AuthService authService() {
        return LazyHolder.authService;
    }

    public static UserService userService() {
        return LazyHolder.userService;
    }

    private static final class LazyHolder {
        private static final AuthService authService = AuthService.getInstance();
        private static final UserService userService = UserService.getInstance();
    }
}
