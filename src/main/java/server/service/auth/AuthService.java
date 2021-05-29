package server.service.auth;

import server.domain.Role;
import server.repository.RepositoryModule;
import server.service.crypto.EncryptService;

import java.time.Instant;
import java.util.Set;

public interface AuthService {
    String authenticate(String userName, String pw);
    String authenticateAsAnonymous();
    void invalidateToken(String token);
    boolean authorize(String token, Role role);
    Set<Role> allRoles(String token);

    static AuthService getInstance() {
        return new AuthServiceImpl(
                new InHeapAutoRemoveTokenRepository(),
                EncryptService.getInstance(),
                RepositoryModule.userRepository(),
                Instant::now,
                7200_000 // 2h
        );
    }
}
