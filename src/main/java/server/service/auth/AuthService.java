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
    // according to Effective Java by Joshua Bloch, we throw Exception only when it's an "exception".
    // Returning false should cover most (expected) cases, including invalid/expired token.
    boolean authorize(String token, Role role);
    Set<Role> allRoles(String token);

    static AuthService getInstance() {
        TimeService timeService = Instant::now;
        return new AuthServiceImpl(
                new InHeapAutoRemovalTokenRepository(timeService),
                EncryptService.getInstance(),
                RepositoryModule.userRepository(),
                timeService,
                7200_000 // 2h
        );
    }
}
