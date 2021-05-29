package server.repository;

public final class RepositoryModule {
    public static UserRepository userRepository() {
        return LazyHolder.userRepository;
    }

    public static RoleRepository roleRepository() {
        return LazyHolder.roleRepository;
    }

    private static final class LazyHolder {
        private static final UserRepository userRepository = new InHeapSimpleUserRepository();
        private static final RoleRepository roleRepository = new InHeapSimpleRoleRepository();
    }
}
