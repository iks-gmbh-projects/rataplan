package de.iks.rataplan.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class JwtConfig {
    public final long lifetime = 60000;
    public final String issuer = "drumdibum-backend";
}
