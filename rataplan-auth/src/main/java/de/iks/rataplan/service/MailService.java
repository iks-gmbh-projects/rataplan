package de.iks.rataplan.service;

import de.iks.rataplan.domain.ResetPasswordMailData;

public interface MailService {
    public void sendMailForResetPassword(ResetPasswordMailData resetPasswordMailData);
}
