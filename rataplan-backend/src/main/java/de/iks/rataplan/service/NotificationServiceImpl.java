package de.iks.rataplan.service;

import de.iks.rataplan.config.FrontendConfig;
import de.iks.rataplan.domain.Vote;
import de.iks.rataplan.domain.VoteNotificationSettings;
import de.iks.rataplan.domain.VoteParticipant;
import de.iks.rataplan.dto.restservice.NotificationType;
import de.iks.rataplan.mapping.crypto.FromEncryptedStringConverter;
import de.iks.rataplan.restservice.AuthService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {
    private final FrontendConfig frontendConfig;
    
    private final AuthService authService;
    
    private final CryptoService cryptoService;
    
    private final FromEncryptedStringConverter fromEncryptedStringConverter;
    
    private final TemplateEngine templateEngine;
    
    @Override
    public void notifyForVoteInvitations(Vote vote) {
        Context ctx = new Context();
        ctx.setVariable("url", getParticipationUrl(vote));
        
        String subjectString = templateEngine.process("invitation_subject", ctx);
        String contentString = templateEngine.process("invitation_content", ctx);
        
        authService.sendMailNotifications(vote.getConsigneeList(),
            NotificationType.INVITE,
            subjectString,
            contentString
        );
        authService.sendUserNotifications(vote.getUserConsignees(),
            NotificationType.INVITE,
            subjectString,
            contentString
        );
    }
    private String getParticipationUrl(Vote vote) {
        String participationToken = vote.getParticipationToken();
        if(participationToken == null) participationToken = vote.getId().toString();
        return frontendConfig.getUrl() + "/vote/" + participationToken;
    }
    @Override
    public void notifyForParticipation(VoteParticipant participant) {
        final Vote vote = participant.getVote();
        
        Context ctx = new Context();
        ctx.setVariable("url", getParticipationUrl(vote));
        ctx.setVariable("title", fromEncryptedStringConverter.convert(vote.getTitle()));
        ctx.setVariable("participant", fromEncryptedStringConverter.convert(participant.getName()));
        
        String subjectString = templateEngine.process("participation_subject", ctx);
        String contentString = templateEngine.process("participation_content", ctx);
        if(vote.getUserId() != null) {
            authService.sendNotification(vote.getUserId(),
                NotificationType.NEW_PARTICIPANT,
                subjectString,
                contentString
            );
        } else if(vote.getNotificationSettings() != null) {
            VoteNotificationSettings notificationSettings = vote.getNotificationSettings();
            if(Objects.requireNonNullElse(notificationSettings.getNotifyParticipation(), false)) {
                authService.sendNotification(cryptoService.decryptDBRaw(notificationSettings.getRecipientEmail()),
                    NotificationType.NEW_PARTICIPANT,
                    subjectString,
                    contentString
                );
            }
        }
    }
    @Override
    public void notifyForParticipationInvalidation(
        Vote vote, Collection<? extends VoteParticipant> affectedParticipants
    )
    {
        Context ctx = new Context();
        ctx.setVariable("link", getParticipationUrl(vote));
        ctx.setVariable("title", fromEncryptedStringConverter.convert(vote.getTitle()));
        
        authService.sendUserNotifications(affectedParticipants.stream()
                .map(VoteParticipant::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableList()),
            NotificationType.PARTICIPATION_INVALIDATED,
            templateEngine.process("participantDeletion_subject", ctx),
            templateEngine.process("participantDeletion_content", ctx)
        );
    }
    
    @Override
    public void notifyForVoteCreation(Vote vote) {
        Context ctx = new Context();
        ctx.setVariable("url", getParticipationUrl(vote));
        ctx.setVariable("adminUrl", getEditUrl(vote));
        
        String subjectContent = templateEngine.process("to_organizerMail_subject", ctx);
        
        String htmlContent = templateEngine.process("to_organizerMail_htmlContent", ctx);
        
        if(vote.getUserId() != null) {
            authService.sendNotification(vote.getUserId(), NotificationType.CREATE, subjectContent, htmlContent);
        } else if(vote.getNotificationSettings() != null) {
            VoteNotificationSettings notificationSettings = vote.getNotificationSettings();
            if(notificationSettings.getSendLinkMail()) {
                authService.sendNotification(cryptoService.decryptDBRaw(notificationSettings.getRecipientEmail()),
                    NotificationType.CREATE,
                    subjectContent,
                    htmlContent
                );
            }
        }
    }
    private String getEditUrl(Vote vote) {
        String editToken = vote.getEditToken();
        return editToken == null ? null : frontendConfig.getUrl() + "/vote/edit/" + editToken;
    }
    
    @Override
    public void notifyForVoteExpired(Vote expiredVote) {
        Context ctx = new Context();
        ctx.setVariable("url", getParticipationUrl(expiredVote));
        ctx.setVariable("title", fromEncryptedStringConverter.convert(expiredVote.getTitle()));
        
        String subjectContent = templateEngine.process("expired_subject", ctx);
        
        String htmlContent = templateEngine.process("expired_content", ctx);
        
        if(expiredVote.getUserId() != null) {
            authService.sendNotification(expiredVote.getUserId(), NotificationType.EXPIRE, subjectContent, htmlContent);
        } else if(expiredVote.getNotificationSettings() != null) {
            VoteNotificationSettings notificationSettings = expiredVote.getNotificationSettings();
            if(notificationSettings.getNotifyExpiration()) {
                authService.sendNotification(cryptoService.decryptDBRaw(notificationSettings.getRecipientEmail()),
                    NotificationType.EXPIRE,
                    subjectContent,
                    htmlContent
                );
            }
        }
    }
}