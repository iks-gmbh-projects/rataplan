package de.iks.rataplan.repository;

import de.iks.rataplan.domain.VoteDecision;
import de.iks.rataplan.domain.VoteDecisionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentDecisionRepository extends JpaRepository<VoteDecision, VoteDecisionId> {
}
