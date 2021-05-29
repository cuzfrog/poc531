package server.service;

public final class ServiceModule {
    public EncryptService encryptService() {
        return new JdkSha256EncryptService(8, 32);
    }
}
