package server.service;

import server.domain.Role;
import server.domain.User;

public interface UserService {
    User createUser(String name, String pw);
    void deleteUser(User user);

    Role createRole(String name);
    void deleteRole(Role role);

    void addRoleToUser(User user, Role roleToAdd);
}
