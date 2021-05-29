package server.repository;

import server.domain.User;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

final class InHeapSimpleUserRepository implements UserRepository {
    private final ConcurrentMap<String, User> users = new ConcurrentHashMap<>();

    @Override
    public void upsert(User user) {
        users.put(user.getName(), user);
    }

    @Override
    public User findByName(String name) {
        return users.get(name);
    }

    @Override
    public void delete(String name) {
        users.remove(name);
    }
}
