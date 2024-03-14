package de.iks.rataplan.service;

import de.iks.rataplan.domain.ConfirmAccountMailData;
import de.iks.rataplan.domain.FeedbackCategory;
import de.iks.rataplan.domain.ParticipantDeletionMailData;
import de.iks.rataplan.domain.ResetPasswordMailData;
import de.iks.rataplan.domain.notifications.NotificationMailData;
import de.iks.rataplan.dto.FeedbackDTO;
import de.iks.rataplan.utils.MailBuilderSendInBlue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sendinblue.ApiException;
import sibApi.TransactionalEmailsApi;
import sibModel.SendSmtpEmail;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Primary
@Service
@RequiredArgsConstructor
@ConditionalOnBean(TransactionalEmailsApi.class)
@Slf4j
public class MailServiceImplSendInBlue implements MailService {
    private final MailBuilderSendInBlue mailBuilder;
    private final TransactionalEmailsApi transactionalEmailsApi;
    
    @Override
    public void sendMailForResetPassword(ResetPasswordMailData resetPasswordMailData) {
        SendSmtpEmail mail = mailBuilder.buildMailForResetPassword(resetPasswordMailData);
        try {
            transactionalEmailsApi.sendTransacEmail(mail);
        } catch (ApiException ex) {
            log.error("API-Exception: {}\n{}\n{}", ex.getCode(), ex.getResponseHeaders(), ex.getResponseBody());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void sendAccountConfirmationEmail(ConfirmAccountMailData confirmAccountMailData) {
        SendSmtpEmail mail = mailBuilder.buildAccountConfirmationEmail(confirmAccountMailData);
        try {
            log.info(mail.getHtmlContent());
            transactionalEmailsApi.sendTransacEmail(mail);
        } catch (ApiException ex) {
            log.error("API-Exception: {}\n{}\n{}", ex.getCode(), ex.getResponseHeaders(), ex.getResponseBody());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void notifyParticipantDeletion(ParticipantDeletionMailData participantDeletionMailData) {
        SendSmtpEmail mail = mailBuilder.buildParticipantDeletionEmail(participantDeletionMailData);
        try {
            transactionalEmailsApi.sendTransacEmail(mail);
        } catch (ApiException ex) {
            log.error("API-Exception: {}\n{}\n{}", ex.getCode(), ex.getResponseHeaders(), ex.getResponseBody());
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public void sendFeedbackReport(Map<FeedbackCategory, ? extends List<? extends FeedbackDTO>> feedback) {
        SendSmtpEmail mail = mailBuilder.buildFeedbackReportMail(feedback);
        try {
            transactionalEmailsApi.sendTransacEmail(mail);
        } catch (ApiException ex) {
            log.error("API-Exception: {}\n{}\n{}", ex.getCode(), ex.getResponseHeaders(), ex.getResponseBody());
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public void sendNotification(String recipient, NotificationMailData notification) {
        SendSmtpEmail mail = mailBuilder.buildNotificationMail(recipient, notification);
        try {
            transactionalEmailsApi.sendTransacEmail(mail);
        } catch (ApiException ex) {
            log.error("API-Exception: {}\n{}\n{}", ex.getCode(), ex.getResponseHeaders(), ex.getResponseBody());
            throw new RuntimeException(ex);
        }
    }
    @Override
    public void sendNotificationSummary(String recipient, Collection<? extends NotificationMailData> notifications) {
        SendSmtpEmail mail = mailBuilder.buildNotificationSummaryMail(recipient, notifications);
        try {
            transactionalEmailsApi.sendTransacEmail(mail);
        } catch (ApiException ex) {
            log.error("API-Exception: {}\n{}\n{}", ex.getCode(), ex.getResponseHeaders(), ex.getResponseBody());
            throw new RuntimeException(ex);
        }
    }
}
