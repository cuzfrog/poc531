package server.service.auth;

import server.domain.Role;
import server.service.crypto.EncryptService;
import server.domain.User;
import server.repository.UserRepository;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

final class AuthServiceImpl implements AuthService {
    static final String ANONYMOUS_TOKEN = UUID.randomUUID().toString();
    private final TokenRepository tokenRepository;
    private final EncryptService encryptService;
    private final UserRepository userRepository;
    private final TimeService timeService;
    private final long tokenDurationMs;

    AuthServiceImpl(TokenRepository tokenRepository,
                    EncryptService encryptService,
                    UserRepository userRepository,
                    TimeService timeService,
                    long tokenDurationMs) {
        this.tokenRepository = tokenRepository;
        this.encryptService = requireNonNull(encryptService);
        this.userRepository = requireNonNull(userRepository);
        this.timeService = requireNonNull(timeService);
        if (tokenDurationMs <= 0) {
            throw new IllegalArgumentException("Token duration must be > 0");
        }
        this.tokenDurationMs = tokenDurationMs;
    }

    @Override
    public String authenticate(String userName, String pw) {
        if (userName == null || userName.isEmpty() || pw == null || pw.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (User.ANONYMOUS_USER.getName().equals(userName)) {
            return ANONYMOUS_TOKEN;
        }

        User user = userRepository.findByName(userName);
        if (user == null) {
            throw new RuntimeException("user not exist, name:" + userName);
        }

        byte[] encryptedPw = encryptService.encrypt(pw, user.getPwSaltStrategy());
        if (!Arrays.equals(user.getPw(), encryptedPw)) {
            throw new RuntimeException("Auth failed");
        }

        Token token = new Token(UUID.randomUUID().toString(), timeService.now().plus(tokenDurationMs, ChronoUnit.MILLIS), user.getRoles());
        tokenRepository.save(token);
        return token.getId();
    }

    @Override
    public String authenticateAsAnonymous() {
        return ANONYMOUS_TOKEN;
    }

    @Override
    public void invalidateToken(String token) {
        tokenRepository.remove(token);
    }

    @Override
    public boolean authorize(String tokenId, Role role) {
        if (ANONYMOUS_TOKEN.equals(tokenId)) {
            return false;
        }
        Token token = tokenRepository.findById(tokenId);
        checkToken(token);
        return token.getRoles().contains(role);
    }

    @Override
    public Set<Role> allRoles(String tokenId) {
        if (ANONYMOUS_TOKEN.equals(tokenId)) {
            return Collections.emptySet();
        }
        Token token = tokenRepository.findById(tokenId);
        checkToken(token);
        return token.getRoles();
    }

    private void checkToken(Token token) {
        if (token == null) {
            throw new RuntimeException("Invalid token");
        }
        if (token.getExpirationTime().isBefore(timeService.now())) {
            throw new RuntimeException("Token expired");
        }
    }
}
