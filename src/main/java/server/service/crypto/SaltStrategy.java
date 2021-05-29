package server.service.crypto;

import java.util.concurrent.ThreadLocalRandom;

public enum SaltStrategy {
    BEGIN_2BYTES, END_2BYTES; // etc

    public static SaltStrategy random() {
        return SaltStrategy.values()[ThreadLocalRandom.current().nextInt(SaltStrategy.values().length)];
    }
}
