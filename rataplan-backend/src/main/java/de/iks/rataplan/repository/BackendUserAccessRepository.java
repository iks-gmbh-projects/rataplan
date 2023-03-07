package de.iks.rataplan.repository;

import de.iks.rataplan.domain.BackendUserAccess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackendUserAccessRepository extends JpaRepository<BackendUserAccess, Integer> {
    public int deleteByUserId(Integer userId);
}
