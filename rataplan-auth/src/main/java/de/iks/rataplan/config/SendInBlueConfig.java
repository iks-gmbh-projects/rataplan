package de.iks.rataplan.config;

import de.iks.rataplan.service.MailServiceImplSendInBlue;
import de.iks.rataplan.utils.MailBuilderSendInBlue;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sendinblue.ApiClient;
import sibApi.TransactionalEmailsApi;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
@ConfigurationProperties("mail.sendinblue")
@ConditionalOnProperty("mail.sendinblue.api-key")
@RequiredArgsConstructor
@Data
@Slf4j
public class SendInBlueConfig {
    private String apiKey;
    private boolean apiKeyFile;
    
    @Bean
    public ApiClient sendInBlueAPIClient() throws IOException {
        if(apiKeyFile) {
            apiKey = Files.readString(Path.of(apiKey))
                .trim();
        }
        ApiClient client = sendinblue.Configuration.getDefaultApiClient();
        client.setApiKey(apiKey);
        return client;
    }
    
    @Primary
    @Bean
    public TransactionalEmailsApi sibEmailsApi() throws IOException {
        return new TransactionalEmailsApi(sendInBlueAPIClient());
    }
    
    @Primary
    @Bean
    public MailServiceImplSendInBlue mailServiceImplSendInBlue(MailBuilderSendInBlue mailBuilderSendInBlue) throws
        IOException
    {
        return new MailServiceImplSendInBlue(mailBuilderSendInBlue, sibEmailsApi());
    }
}