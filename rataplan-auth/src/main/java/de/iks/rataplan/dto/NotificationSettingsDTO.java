package de.iks.rataplan.dto;

import de.iks.rataplan.domain.notifications.EmailCycle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingsDTO {
    private EmailCycle defaultSettings;
    private Map<String, EmailCycle> categorySettings;
}
