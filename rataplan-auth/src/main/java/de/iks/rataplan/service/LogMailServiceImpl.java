package de.iks.rataplan.service;

import de.iks.rataplan.domain.ConfirmAccountMailData;
import de.iks.rataplan.domain.ResetPasswordMailData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "RATAPLAN.PROD", havingValue = "false", matchIfMissing = true)
@Slf4j
public class LogMailServiceImpl implements MailService {
    @Value("${rataplan.frontend.url}")
    private String baseUrl;
    @Override
    public void sendMailForResetPassword(ResetPasswordMailData resetPasswordMailData) {
        log.info("Reset password link: {}", baseUrl + "/reset-password?token=" + resetPasswordMailData.getToken());
    }

    @Override
    public void sendAccountConfirmationEmail(ConfirmAccountMailData confirmAccountMailData) {
        log.info("Account confirmation link: " + baseUrl + "/confirm-account/" + confirmAccountMailData.getToken());
    }
}
