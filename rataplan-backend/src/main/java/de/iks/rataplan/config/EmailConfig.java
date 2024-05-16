package de.iks.rataplan.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import sibModel.SendSmtpEmailSender;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("mail")
@RequiredArgsConstructor
@Data
public class EmailConfig {
    private String address;
    private String name;
    private String contactTo;
    
    @Bean
    public SendSmtpEmailSender sendSmtpEmailSender() {
        return new SendSmtpEmailSender()
            .name(name)
            .email(address);
    }
}