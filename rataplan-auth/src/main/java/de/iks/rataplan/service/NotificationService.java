package de.iks.rataplan.service;

import de.iks.rataplan.domain.notifications.EmailCycle;
import de.iks.rataplan.dto.NotificationSettingsDTO;
import de.iks.rataplan.dto.NotificationDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface NotificationService {
    NotificationSettingsDTO getNotificationSettings(int user);
    Map<String, List<String>> getNotificationTypes();
    NotificationSettingsDTO updateNotificationSettings(int user, NotificationSettingsDTO settings);
    void notify(Collection<? extends NotificationDTO> notifications);
    void sendSummary(Set<EmailCycle> sendCycle);
}
