package poc.genericresourcemanagement.interfaces.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

public class BigDecimalJsonSerializer extends JsonSerializer<BigDecimal> {
    @Override
    public void serialize(final BigDecimal value, final JsonGenerator gen, final SerializerProvider serializers)
            throws IOException {
        gen.writeString(value.stripTrailingZeros().toPlainString());
    }

    @Override
    public Class<BigDecimal> handledType() {
        return BigDecimal.class;
    }
}
