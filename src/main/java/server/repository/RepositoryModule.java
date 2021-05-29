package server.repository;

public final class RepositoryModule {
    public static UserRepository userRepository() {
        return new InHeapSimpleUserRepository();
    }

    public static RoleRepository roleRepository() {
        return new InHeapSimpleRoleRepository();
    }
}
