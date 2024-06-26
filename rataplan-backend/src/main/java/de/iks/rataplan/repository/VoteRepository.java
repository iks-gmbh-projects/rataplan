package de.iks.rataplan.repository;

import de.iks.rataplan.domain.Vote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.List;
import java.util.stream.Stream;

public interface VoteRepository extends JpaRepository<Vote, Integer> {
    List<Vote> findAllByUserId(Integer userId);
    
    int deleteAllByUserId(Integer userId);
    
    @Query("SELECT DISTINCT v FROM Vote v WHERE :userId IN (SELECT p.id FROM v.participants p)")
    List<Vote> findDistinctByParticipantIn(Integer userId);
    
    List<Vote> findByDeadlineBeforeAndNotifiedFalse(Date deadline);// find by deadline == xx and organizermail not null
    
    Vote findByParticipationToken(String participationToken);
    
    Vote findByEditToken(String editToken);
    
    @Query(
        value = "SELECT * FROM vote" + " WHERE title NOT LIKE 'ENC\\_\\_##\\_\\_%'" +
                " OR description NOT LIKE 'ENC\\_\\_##\\_\\_%'" +
                " OR (organizername IS NOT NULL AND organizername NOT LIKE 'ENC\\_\\_##\\_\\_%')",
        nativeQuery = true
    )
    Stream<Vote> findUnencrypted();
}
