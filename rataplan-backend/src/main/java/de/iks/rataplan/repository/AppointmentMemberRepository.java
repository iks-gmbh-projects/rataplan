package de.iks.rataplan.repository;

import de.iks.rataplan.domain.AppointmentMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.stream.Stream;

public interface AppointmentMemberRepository extends JpaRepository<AppointmentMember, Integer> {
    @Query(value = "SELECT * FROM appointmentMember" +
        " WHERE name NOT LIKE 'ENC\\_\\_##\\_\\_%'", nativeQuery = true)
    public Stream<AppointmentMember> findUnencrypted();
}
