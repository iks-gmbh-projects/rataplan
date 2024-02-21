package de.iks.rataplan.dto.restservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationDTO {
    private Integer recipientId;
    private String type;
    private String subject;
    private String content;
    private String summaryContent;
}
