package server.service.auth;

import server.domain.Role;

import java.util.Set;

public interface AuthService {
    String authenticate(String userName, String pw);
    void invalidateToken(String token);
    boolean authorize(String token, Role role);
    Set<Role> allRoles(String token);
}
