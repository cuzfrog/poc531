package server.service.user;

import server.domain.Role;
import server.domain.User;
import server.repository.RepositoryModule;
import server.service.crypto.EncryptService;

public interface UserService {
    User createUser(String name, String pw);
    void deleteUser(User user);

    Role createRole(String name);
    void deleteRole(Role role);

    void addRoleToUser(User user, Role roleToAdd);

    static UserService getInstance() {
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
