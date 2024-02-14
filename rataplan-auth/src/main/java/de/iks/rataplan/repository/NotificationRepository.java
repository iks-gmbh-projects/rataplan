package de.iks.rataplan.repository;

import de.iks.rataplan.domain.notifications.EmailCycle;
import de.iks.rataplan.domain.notifications.Notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.stream.Stream;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query(
        value = "SELECT n.* FROM notification_queue AS n JOIN rataplanuser AS u ON n.recipientId = u.id JOIN notification_type AS t ON n.typeId = t.id LEFT JOIN notification_type_setting AS ts ON n.recipientId = ts.userId AND n.typeId = ts.typeId LEFT JOIN notification_category_setting AS cs ON n.recipientId = cs.userId AND t.categoryId = cs.categoryId WHERE COALESCE(ts.emailCycle, cs.emailCycle, u.defaultEmailCycle) = :#{#cycle.ordinal()}",
        nativeQuery = true
    )
    Stream<Notification> findCycleNotifications(EmailCycle cycle);
}
