package de.iks.rataplan.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("backend.surveytool.urltemplate")
@Data
public class SurveyToolMessageConfig {
    private String delete;
    private String anonymize;
}
