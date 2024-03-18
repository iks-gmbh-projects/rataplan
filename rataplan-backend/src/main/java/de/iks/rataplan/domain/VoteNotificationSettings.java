package de.iks.rataplan.domain;

import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class VoteNotificationSettings {
    private byte[] recipientEmail;
    private Boolean sendLinkMail;
    private Boolean notifyParticipation;
    private Boolean notifyExpiration;
    
    @Transient
    public Boolean getSendLinkMail() {
        return sendLinkMail;
    }
}
