package de.iks.rataplan.repository;

import de.iks.rataplan.domain.AppointmentDecision;
import de.iks.rataplan.domain.VoteDecisionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentDecisionRepository extends JpaRepository<AppointmentDecision, VoteDecisionId> {
}
