package de.iks.rataplan.repository;

import de.iks.rataplan.domain.notifications.NotificationType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationTypeRepository extends JpaRepository<NotificationType, Long> {
    Optional<NotificationType> findByName(String name);
}
