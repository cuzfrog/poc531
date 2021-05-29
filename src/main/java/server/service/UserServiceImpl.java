package server.service;

import server.domain.Role;
import server.domain.SaltStrategy;
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
        if (name == null || name.isEmpty() || pw == null || pw.isEmpty()) {
            throw new IllegalArgumentException();
        }

        User existingUser = userRepository.findByName(name);
        if (existingUser != null) {
            throw new RuntimeException("User already exists, name:" + name);
        }

        SaltStrategy saltStrategy = SaltStrategy.random();

        User user = new User();
        user.setName(name);
        user.setPwSaltStrategy(saltStrategy);
        user.setPw(encryptService.encrypt(pw, saltStrategy));

        userRepository.upsert(user);
        return user;
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public Role createRole(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }

        Role existingRole = roleRepository.findByName(name);
        if (existingRole != null) {
            throw new RuntimeException("Role already exists, name:" + name);
        }

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
        // TODO, validate role
        user.addRole(roleToAdd);
        userRepository.upsert(user);
    }
}
