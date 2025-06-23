package de.iks.rataplan.config;

import de.iks.rataplan.service.MailServiceImplSendInBlue;
import de.iks.rataplan.utils.MailBuilderSendInBlue;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import sendinblue.ApiClient;
import sibApi.TransactionalEmailsApi;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ConfigurationProperties("mail.sendinblue")
@ConditionalOnProperty("mail.sendinblue.api-key")
@RequiredArgsConstructor
@Data
public class SendInBlueConfig {
    private String apiKey;
    
    @Bean
    public ApiClient sendInBlueAPIClient() {
        ApiClient client = sendinblue.Configuration.getDefaultApiClient();
        client.setApiKey(apiKey);
        return client;
    }
    
    @Primary
    @Bean
    public TransactionalEmailsApi sibEmailsApi() {
        return new TransactionalEmailsApi(sendInBlueAPIClient());
    }
    
    @Primary
    @Bean
    public MailServiceImplSendInBlue mailServiceImplSendInBlue(MailBuilderSendInBlue mailBuilderSendInBlue) {
        return new MailServiceImplSendInBlue(mailBuilderSendInBlue, sibEmailsApi());
    }
}