package server.test.support.junit;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

public final class ByteArrayConverter implements ArgumentConverter {
    @Override
    public Object convert(Object o, ParameterContext parameterContext) throws ArgumentConversionException {
        if (o instanceof String) {
            String expr = (String) o;
            if (expr.startsWith("[") && expr.endsWith("]")) {
                String[] params = expr.substring(1, expr.length() - 1).split("\\s+");
                byte[] bytes = new byte[params.length];
                for (int i = 0; i < params.length; i++) {
                    bytes[i] = Byte.parseByte(params[i]);
                }
                return bytes;
            }
        }
        throw new ArgumentConversionException("TODO");
    }
}
