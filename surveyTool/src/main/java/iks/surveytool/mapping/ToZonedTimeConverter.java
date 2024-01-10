package iks.surveytool.mapping;

import lombok.NoArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@NoArgsConstructor
public class ToZonedTimeConverter extends AbstractConverter<Instant, ZonedDateTime> {
    @Override
    protected ZonedDateTime convert(Instant source) {
        return source.atZone(ZoneId.systemDefault());
    }
}
