package de.iks.rataplan.runner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import sendinblue.ApiClient;
import sibApi.AccountApi;

@Component
@ConditionalOnBean(ApiClient.class)
public class SendInBlueConfirmer implements ApplicationRunner {
    private final AccountApi accountApi;
    
    public SendInBlueConfirmer(ApiClient apiClient) {
        this.accountApi = new AccountApi(apiClient);
    }
    
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        accountApi.getAccount(); //will throw exception if invalid API key
    }
}
