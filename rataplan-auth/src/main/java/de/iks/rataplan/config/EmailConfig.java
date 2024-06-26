package de.iks.rataplan.config;

import de.iks.rataplan.service.MailService;
import de.iks.rataplan.service.MailServiceImplSendInBlue;
import de.iks.rataplan.utils.MailBuilderSendInBlue;
import lombok.Data;

import org.springframework.context.annotation.Primary;

import sendinblue.ApiClient;
import sibApi.TransactionalEmailsApi;
import sibModel.SendSmtpEmailSender;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("mail")
@Data
public class EmailConfig {
    private String address;
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
    
    @Primary
    @Bean
    @ConditionalOnBean(ApiClient.class)
    public MailServiceImplSendInBlue mailService(
        MailBuilderSendInBlue mailBuilderSendInBlue,
        ApiClient api
    ) {
        return new MailServiceImplSendInBlue(mailBuilderSendInBlue, transactionalEmailsApi(api));
    }
    
    @Bean
    public SendSmtpEmailSender sendSmtpEmailSender() {
        return new SendSmtpEmailSender()
            .name(name)
            .email(address);
    }
}