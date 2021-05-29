package server.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import server.domain.SaltStrategy;
import server.test.support.junit.ByteArrayConverter;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class JdkSha256EncryptServiceTest {
    @Disabled
    @Test
    void encrypt() {
        JdkSha256EncryptService service = new JdkSha256EncryptService(3, 32);
        assertThat(service.encrypt("abc", SaltStrategy.BEGIN_2BYTES))
                .isEqualTo(new byte[]{}); // TODO: use independent source
    }

    @ParameterizedTest
    @CsvSource({
            //saltStrategy, expected
            "BEGIN_2BYTES, [1 2]",
            "END_2BYTES,   [2 3]",
    })
    void getSalt(SaltStrategy saltStrategy, @ConvertWith(ByteArrayConverter.class) byte[] expected) {
        byte[] input = new byte[]{1,2,3};
        assertThat(JdkSha256EncryptService.getSalt(input, saltStrategy)).isEqualTo(expected);
    }
}
