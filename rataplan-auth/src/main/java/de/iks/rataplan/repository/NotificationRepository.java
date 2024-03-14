package de.iks.rataplan.repository;

import de.iks.rataplan.domain.notifications.EmailCycle;
import de.iks.rataplan.domain.notifications.Notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.stream.Stream;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query(
        value = "SELECT n.* FROM notification_queue AS n JOIN rataplanuser AS u ON n.recipientId = u.id LEFT JOIN notification_setting AS s ON n.recipientId = s.userId AND n.categoryId = s.categoryId WHERE s.emailCycle = :cycle OR (u.defaultEmailCycle = :cycle AND s.emailCycle IS NULL)",
        nativeQuery = true
    )
    Stream<Notification> findCycleNotifications(EmailCycle cycle);
}
