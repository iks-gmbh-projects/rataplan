package de.iks.rataplan.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sendinblue.ApiClient;
import sibApi.TransactionalEmailsApi;
import sibModel.SendSmtpEmailSender;

@Configuration
public class EmailConfig {
    @Value("${mail.sendinblue.api_key}")
    private String apiKey;
    @Value("${mail.address}")
    private String address;
    @Value("${mail.name}")
    private String name;
    @Bean
    public ApiClient apiClient() {
        ApiClient ret = sendinblue.Configuration.getDefaultApiClient();
        ret.setApiKey(apiKey);
        return ret;
    }
    @Bean
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
