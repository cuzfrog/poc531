package server.service.user;

import server.domain.Role;
import server.domain.User;
import server.repository.RoleRepository;
import server.repository.UserRepository;

import static java.util.Objects.requireNonNull;

final class UserServiceValidateProxy implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService delegate;

    UserServiceValidateProxy(UserRepository userRepository, RoleRepository roleRepository, UserService delegate) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.delegate = requireNonNull(delegate);
    }

    @Override
    public User createUser(String name, String pw) {
        if (name == null || name.isEmpty() || pw == null || pw.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (User.ANONYMOUS_USER.getName().equals(name)) {
            throw new IllegalArgumentException();
        }
        User existingUser = userRepository.findByName(name);
        if (existingUser != null) {
            throw new RuntimeException("User already exists, name:" + name);
        }

        return delegate.createUser(name, pw);
    }

    @Override
    public void deleteUser(String name) {
        if (User.ANONYMOUS_USER.getName().equals(name)) {
            throw new IllegalArgumentException();
        }
        delegate.deleteUser(name);
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
        return delegate.createRole(name);
    }

    @Override
    public void deleteRole(String name) {
        delegate.deleteRole(name);
    }

    @Override
    public User addRoleToUser(String userName, Role roleToAdd) {
        if (userName == null || roleToAdd == null) {
            throw new IllegalArgumentException();
        }
        if (User.ANONYMOUS_USER.getName().equals(userName)) {
            throw new IllegalArgumentException("Role can't be added to the Anonymous user");
        }
        User existingUser = userRepository.findByName(userName);
        if (existingUser == null) {
            throw new RuntimeException("User does not exist");
        }
        if (existingUser.getRoles().contains(roleToAdd)) {
            return existingUser;
        }
        Role role = roleRepository.findByName(roleToAdd.getName());
        if (role == null) {
            throw new RuntimeException("Role does not exist");
        }

        return delegate.addRoleToUser(userName, roleToAdd);
    }
}
