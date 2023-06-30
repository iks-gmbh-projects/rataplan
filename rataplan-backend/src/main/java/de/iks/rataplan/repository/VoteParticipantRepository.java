package de.iks.rataplan.repository;

import de.iks.rataplan.domain.VoteParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.stream.Stream;

public interface VoteParticipantRepository extends JpaRepository<VoteParticipant, Integer> {
    @Query(value = "SELECT * FROM voteParticipant" +
        " WHERE name NOT LIKE 'ENC\\_\\_##\\_\\_%'", nativeQuery = true)
    public Stream<VoteParticipant> findUnencrypted();
    public Stream<VoteParticipant> findByUserId(Integer userId);
}
