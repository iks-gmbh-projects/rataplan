package de.iks.rataplan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.iks.rataplan.domain.BackendUser;

public interface BackendUserRepository extends JpaRepository<BackendUser, Integer>  {

	BackendUser findOneByAuthUserId(Integer authUserId);

}
