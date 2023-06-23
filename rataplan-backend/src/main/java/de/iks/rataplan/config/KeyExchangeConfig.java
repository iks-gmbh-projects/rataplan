package de.iks.rataplan.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Data
public class KeyExchangeConfig {
    @Value("${auth.key.url:}")
    private String url;
    @Value("${auth.displayname.url:}")
    private String displayNameURL;
    @Value("${auth.key.cachetime:600}")
    private long cachetime;
    @Value("${auth.key.shortenedCache:true}")
    private boolean shortenedCache;
    @Value("#{${auth.id.validIssuer:{'drumdibum-auth'}}}")
    private List<String> validIssuer;
}
