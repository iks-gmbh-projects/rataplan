package de.iks.rataplan.repository;

import de.iks.rataplan.domain.Feedback;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.stream.Stream;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Stream<Feedback> findBySent(boolean sent);
}
