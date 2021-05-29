package server.service;

import server.repository.RepositoryModule;
import server.service.auth.AuthService;
import server.service.crypto.EncryptService;

public final class ServiceModule {
    public static AuthService authService() {
        return AuthService.getInstance();
    }

    public static UserService userService() {
        UserService actual = new UserServiceImpl(
                EncryptService.getInstance(),
                RepositoryModule.userRepository(),
                RepositoryModule.roleRepository()
        );
        return new UserServiceValidateProxy(
                RepositoryModule.userRepository(),
                RepositoryModule.roleRepository(),
                actual
        );
    }
}
