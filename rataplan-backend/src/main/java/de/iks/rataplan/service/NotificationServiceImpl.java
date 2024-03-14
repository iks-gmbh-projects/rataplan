package de.iks.rataplan.service;

import de.iks.rataplan.domain.Vote;
import de.iks.rataplan.dto.restservice.NotificationType;
import de.iks.rataplan.mapping.crypto.FromEncryptedStringConverter;
import de.iks.rataplan.restservice.AuthService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {
    @Value("${rataplan.frontend.url}")
    private String baseUrl;
    
    private final AuthService authService;
    
    private final TemplateEngine templateEngine;
    
    private final FromEncryptedStringConverter fromEncryptedStringConverter;
    
    @Override
    public void notifyForVoteInvitations(Vote vote) {
        String participationToken = vote.getParticipationToken();
        if(participationToken == null) participationToken = vote.getId().toString();
        String url = baseUrl + "/vote/" + participationToken;
        
        
        Context ctx = new Context();
        ctx.setVariable("url", url);
        
        String subjectString = templateEngine.process("invitation_subject", ctx);
        String contentString = templateEngine.process("invitation_content", ctx);
        
        authService.sendMailNotifications(
            vote.getConsigneeList(),
            NotificationType.INVITE,
            subjectString,
            contentString
        );
    }
    @Override
    public void notifyForParticipationInvalidation(Vote vote, Collection<Integer> affectedParticipants) {
        //TODO
    }
}
