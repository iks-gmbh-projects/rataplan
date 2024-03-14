package de.iks.rataplan.repository;

import de.iks.rataplan.domain.notifications.NotificationCategory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationCategoryRepository extends JpaRepository<NotificationCategory, Long> {
    Optional<NotificationCategory> findByName(String name);
}
