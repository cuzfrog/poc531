package server.service;

import server.domain.SaltStrategy;

interface EncryptService {
    byte[] encrypt(String input, SaltStrategy saltStrategy);
}
