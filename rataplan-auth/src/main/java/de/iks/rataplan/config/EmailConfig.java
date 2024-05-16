package de.iks.rataplan.config;

import lombok.Data;
import sibModel.SendSmtpEmailSender;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties("mail")
@Data
public class EmailConfig {
    private String address;
    private String name;
    private List<String> feedback;
    
    @Bean
    public SendSmtpEmailSender sendSmtpEmailSender() {
        return new SendSmtpEmailSender().name(name).email(address);
    }
}