package de.iks.rataplan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import de.iks.rataplan.domain.BackendUserAccess;

public interface BackendUserAccessRepository extends JpaRepository<BackendUserAccess, Integer> {

	@Transactional
	@Modifying
	@Query("DELETE FROM BackendUserAccess bua WHERE bua.id = :id")
	void deleteAsdf(@Param("id") Integer id);
	
	@Transactional
	@Modifying
	@Query("UPDATE BackendUserAccess bua SET isEdit = false WHERE bua.appointmentRequestId = :requestId AND bua.backendUserId <> :ownerId")
	void resetAdminAccess(@Param("requestId") Integer requestId, @Param("ownerId") Integer ownerId);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM BackendUserAccess bua WHERE bua.appointmentRequestId = :requestId AND isEdit = false")
	void resetAccess(@Param("requestId") Integer requestId);

}
