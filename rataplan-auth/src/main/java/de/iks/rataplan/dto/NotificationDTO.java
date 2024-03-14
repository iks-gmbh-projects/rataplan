package de.iks.rataplan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Integer recipientId;
    private String recipientEmail;
    private String category;
    private String subject;
    private String content;
    private String summaryContent;
}
