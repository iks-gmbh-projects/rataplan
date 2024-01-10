package de.iks.rataplan.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class KeyExchangeConfig {
    @Value("${auth.key.url:}")
    private String url;
    @Value("${auth.displayname.url:}")
    private String displayNameURL;
    @Value("${auth.email.url:}")
    private String emailURL;
    @Value("${auth.id.validIssuer:drumdibum-auth}")
    private String validIssuer;
}
