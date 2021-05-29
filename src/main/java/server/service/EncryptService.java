package server.service;

import server.domain.SaltStrategy;

public interface EncryptService {
    byte[] encrypt(String input, SaltStrategy saltStrategy);
}
