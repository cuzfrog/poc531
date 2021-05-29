package server.service;

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
    public void deleteUser(User user) {
        if (User.ANONYMOUS_USER.equals(user)) {
            throw new IllegalArgumentException();
        }
        delegate.deleteUser(user);
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
    public void deleteRole(Role role) {
        delegate.deleteRole(role);
    }

    @Override
    public void addRoleToUser(User user, Role roleToAdd) {
        if (user == null || roleToAdd == null) {
            throw new IllegalArgumentException();
        }
        if (User.ANONYMOUS_USER.equals(user)) {
            throw new IllegalArgumentException("Role can't be added to the Anonymous user");
        }
        if (user.getRoles().contains(roleToAdd)) {
            return;
        }
        Role role = roleRepository.findByName(roleToAdd.getName());
        if (role == null) {
            throw new RuntimeException("Role does not exist");
        }
        delegate.addRoleToUser(user, roleToAdd);
    }
}
