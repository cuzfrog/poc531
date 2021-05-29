package server.service;

import server.domain.User;

public interface UserService {
    User createUser(String name, String pw);
    void deleteUser(User user);
}
