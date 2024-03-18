package de.iks.rataplan.service;

import de.iks.rataplan.domain.Vote;
import de.iks.rataplan.domain.VoteParticipant;
import de.iks.rataplan.dto.restservice.NotificationType;
import de.iks.rataplan.restservice.AuthService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {
    @Value("${rataplan.frontend.url}")
    private String baseUrl;
    
    private final AuthService authService;
    
    private final TemplateEngine templateEngine;
    
    @Override
    public void notifyForVoteInvitations(Vote vote) {
        String participationToken = vote.getParticipationToken();
        if(participationToken == null) participationToken = vote.getId().toString();
        String url = baseUrl + "/vote/" + participationToken;
        
        Context ctx = new Context();
        ctx.setVariable("url", url);
        
        String subjectString = templateEngine.process("invitation_subject", ctx);
        String contentString = templateEngine.process("invitation_content", ctx);
        
        authService.sendMailNotifications(vote.getConsigneeList(),
            NotificationType.INVITE,
            subjectString,
            contentString
        );
    }
    @Override
    public void notifyForParticipationInvalidation(
        Vote vote, Collection<? extends VoteParticipant> affectedParticipants
    )
    {
        String voteLink = baseUrl + "/vote/" + vote.getParticipationToken();
        
        Context ctx = new Context();
        ctx.setVariable("link", voteLink);
        
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
        String participationToken = vote.getParticipationToken();
        if(participationToken == null) participationToken = vote.getId().toString();
        String url = baseUrl + "/vote/" + participationToken;
        String editToken = vote.getEditToken();
        String adminUrl = editToken == null ? null : baseUrl + "/vote/" + editToken + "/edit";
        
        Context ctx = new Context();
        ctx.setVariable("url", url);
        ctx.setVariable("adminUrl", adminUrl);
        
        String subjectContent = templateEngine.process("to_organizerMail_subject", ctx);
        
        //		String plainContent = createPlainContent(url, adminUrl);
        //		content.setType("text/plain");
        //		content.setValue(plainContent);
        //		mail.addContent(content);
        
        String htmlContent = templateEngine.process("to_organizerMail_htmlContent", ctx);
        
        if(vote.getUserId() != null) {
            authService.sendNotification(
                vote.getUserId(),
                NotificationType.CREATE,
                subjectContent,
                htmlContent
            );
        }
    }
}
