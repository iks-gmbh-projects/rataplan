package de.iks.rataplan.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.iks.rataplan.domain.AppointmentRequest;

public interface AppointmentRequestRepository extends JpaRepository<AppointmentRequest, Integer> {
	List<AppointmentRequest> findAllByBackendUserId(Integer backendUserId);
	
	List<AppointmentRequest> findByAppointmentMembers_BackendUserIdIn(Integer backendUserId);
	
	List<AppointmentRequest> findByDeadlineBeforeAndExpiredFalse(Date deadline);// find by deadline == xx and organizermail not null

	AppointmentRequest findByParticipationToken(String participationToken);

	AppointmentRequest findByEditToken(String editToken);
}
