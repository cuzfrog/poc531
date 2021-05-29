package server.service.crypto;

import server.domain.SaltStrategy;

public interface EncryptService {
    byte[] encrypt(String input, SaltStrategy saltStrategy);

    static EncryptService getInstance() {
        return new JdkSha256EncryptService(8, 32); // keep it simple, not to ensure a strict singleton. could be taken care of by a DI container
    }
}
