package de.iks.rataplan.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sendinblue.ApiClient;
import sibApi.TransactionalEmailsApi;
import sibModel.SendSmtpEmailSender;

@Configuration
public class EmailConfig {
    @Value("${mail.address}")
    private String address;
    @Value("${mail.name}")
    private String name;
    @Bean
    @ConditionalOnProperty("mail.sendinblue.api_key")
    public ApiClient apiClient(
        @Value("${mail.sendinblue.api_key}") String apiKey
    ) {
        ApiClient ret = sendinblue.Configuration.getDefaultApiClient();
        ret.setApiKey(apiKey);
        return ret;
    }
    @Bean
    @ConditionalOnBean(ApiClient.class)
    public TransactionalEmailsApi transactionalEmailsApi(ApiClient apiClient) {
        return new TransactionalEmailsApi(apiClient);
    }
    
    @Bean
    public SendSmtpEmailSender sendSmtpEmailSender() {
        return new SendSmtpEmailSender()
            .name(name)
            .email(address);
    }
}
