package server.service;

import server.domain.Role;
import server.service.crypto.SaltStrategy;
import server.domain.User;
import server.repository.RoleRepository;
import server.repository.UserRepository;
import server.service.crypto.EncryptService;

import static java.util.Objects.requireNonNull;

final class UserServiceImpl implements UserService {
    private final EncryptService encryptService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    UserServiceImpl(EncryptService encryptService, UserRepository userRepository, RoleRepository roleRepository) {
        this.encryptService = requireNonNull(encryptService);
        this.userRepository = requireNonNull(userRepository);
        this.roleRepository = requireNonNull(roleRepository);
    }

    @Override
    public User createUser(String name, String pw) {
        SaltStrategy saltStrategy = SaltStrategy.random();

        User user = User.builder()
                .withName(name)
                .withPw(encryptService.encrypt(pw, saltStrategy))
                .withPwSaltStrategy(saltStrategy).build();

        userRepository.upsert(user);
        return user;
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public Role createRole(String name) {
        Role role = new Role(name);
        roleRepository.upsert(role);
        return role;
    }

    @Override
    public void deleteRole(Role role) {
        roleRepository.delete(role);
    }

    @Override
    public void addRoleToUser(User user, Role roleToAdd) {
        userRepository.upsert(user.update().addRole(roleToAdd).build());
    }
}
