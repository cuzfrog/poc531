package server.service.crypto;

import server.domain.SaltStrategy;

public interface EncryptService {
    byte[] encrypt(String input, SaltStrategy saltStrategy);

    static EncryptService encryptService() {
        return new JdkSha256EncryptService(8, 32);
    }
}
