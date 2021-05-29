package server.repository;

import server.domain.User;

public interface UserRepository {
    void upsert(User user);
    User findByName(String name);
    void delete(User user);
}
