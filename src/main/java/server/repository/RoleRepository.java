package server.repository;

import server.domain.Role;

public interface RoleRepository {
    void upsert(Role role);
    Role findByName(String name);
    void delete(String name);
}
