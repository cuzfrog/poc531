package server.repository;

import server.domain.Role;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

final class InHeapSimpleRoleRepository implements RoleRepository {
    private final ConcurrentMap<String, Role> roles = new ConcurrentHashMap<>();

    @Override
    public void upsert(Role role) {
        roles.put(role.getName(), role);
    }

    @Override
    public Role findByName(String name) {
        return roles.get(name);
    }

    @Override
    public void delete(Role role) {
        roles.remove(role.getName());
    }
}
