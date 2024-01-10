package iks.surveytool.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class KeyExchangeConfig {
    @Value("${auth.key.url:}")
    private String url;
    @Value("${auth.id.validIssuer:drumdibum-auth}")
    private String validIssuer;
}
