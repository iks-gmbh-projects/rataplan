package iks.surveytool.mapping;

import lombok.NoArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZonedDateTime;

@Component
@NoArgsConstructor
public class ToInstantConverter extends AbstractConverter<ZonedDateTime, Instant> {
    @Override
    protected Instant convert(ZonedDateTime source) {
        return source.toInstant();
    }
}
