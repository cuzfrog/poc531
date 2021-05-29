package server.service;

import server.domain.SaltStrategy;
import server.domain.User;
import server.repository.UserRepository;

final class UserServiceImpl implements UserService {
    private final EncryptService encryptService;
    private final UserRepository userRepository;

    UserServiceImpl(EncryptService encryptService, UserRepository userRepository) {
        this.encryptService = encryptService;
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(String name, String pw) {
        if (name == null || name.isEmpty() || pw == null || pw.isEmpty()) {
            throw new IllegalArgumentException();
        }

        User existingUser = userRepository.findByName(name);
        if (existingUser != null) {
            throw new RuntimeException("User already exists");
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
}
