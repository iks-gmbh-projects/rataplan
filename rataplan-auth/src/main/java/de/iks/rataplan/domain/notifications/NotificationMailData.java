package de.iks.rataplan.domain.notifications;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationMailData {
    private String subject;
    private String content;
}
