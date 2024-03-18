package de.iks.rataplan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteNotificationSettingsDTO {
    private String recipientEmail;
    private boolean sendLinkMail;
    private boolean notifyParticipation;
    private boolean notifyExpiration;
}
