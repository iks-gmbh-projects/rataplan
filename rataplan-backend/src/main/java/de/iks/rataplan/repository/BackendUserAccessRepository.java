package de.iks.rataplan.repository;

import de.iks.rataplan.domain.BackendUserAccess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.stream.Stream;

public interface BackendUserAccessRepository extends JpaRepository<BackendUserAccess, Integer> {
    public int deleteByUserId(Integer userId);
    public Optional<BackendUserAccess> findByVoteIdAndUserId(int voteId, Integer userId);
    public Stream<BackendUserAccess> findByInvitedIsTrueAndUserId(int userId);
}
