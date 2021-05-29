package server.service;

import server.domain.SaltStrategy;
import server.support.annotation.VisibleForTesting;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

final class JdkSha256EncryptService implements EncryptService {
    private final int minInputStrLength;
    private final int maxInputStrLength;
    private final MessageDigest digest;

    JdkSha256EncryptService(int minInputStrLength, int maxInputStrLength) {
        this.minInputStrLength = minInputStrLength;
        this.maxInputStrLength = maxInputStrLength;
        try {
            digest = MessageDigest.getInstance("SHA3-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    // salting here just for illustration
    @Override
    public byte[] encrypt(String input, SaltStrategy saltStrategy) {
        if (input == null || input.length() < minInputStrLength || input.length() > maxInputStrLength) {
            throw new IllegalArgumentException();
        }
        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
        byte[] salt = getSalt(inputBytes, saltStrategy);

        byte[] concatedBytes = Arrays.copyOf(inputBytes, inputBytes.length + salt.length);
        System.arraycopy(salt, 0, concatedBytes, inputBytes.length, salt.length);

        return digest.digest(concatedBytes);
    }

    @VisibleForTesting
    static byte[] getSalt(byte[] input, SaltStrategy saltStrategy) {
        final byte[] salt;
        if (saltStrategy == SaltStrategy.BEGIN_2BYTES) {
            salt = Arrays.copyOf(input, 2);
        } else if (saltStrategy == SaltStrategy.END_2BYTES) {
            salt = Arrays.copyOfRange(input, input.length-2, input.length);
        } else {
            throw new UnsupportedOperationException("Unknown salt strategy:" + saltStrategy);
        }
        return salt;
    }
}
