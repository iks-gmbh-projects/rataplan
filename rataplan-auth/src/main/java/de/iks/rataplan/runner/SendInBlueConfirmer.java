package de.iks.rataplan.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import sendinblue.ApiClient;
import sibApi.AccountApi;

@Component
@Slf4j
@ConditionalOnBean(ApiClient.class)
public class SendInBlueConfirmer implements ApplicationRunner {
    private final AccountApi accountApi;
    
    public SendInBlueConfirmer(ApiClient apiClient) {
        this.accountApi = new AccountApi(apiClient);
    }
    
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        log.info("SendInBlue Account: {}", accountApi.getAccount());
    }
}
