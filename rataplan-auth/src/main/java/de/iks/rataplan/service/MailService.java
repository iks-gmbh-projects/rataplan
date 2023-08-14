package de.iks.rataplan.service;

import de.iks.rataplan.domain.ConfirmAccountMailData;
import de.iks.rataplan.domain.ParticipantDeletionMailData;
import de.iks.rataplan.domain.ResetPasswordMailData;
import sendinblue.ApiException;

public interface MailService {
    public void sendMailForResetPassword(ResetPasswordMailData resetPasswordMailData);

    public void sendAccountConfirmationEmail(ConfirmAccountMailData confirmAccountMailData);
    public void notifyParticipantDeletion(ParticipantDeletionMailData participantDeletionMailData) throws ApiException;
}
