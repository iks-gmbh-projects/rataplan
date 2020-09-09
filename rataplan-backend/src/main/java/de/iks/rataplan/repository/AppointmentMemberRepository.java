package de.iks.rataplan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.iks.rataplan.domain.AppointmentMember;

public interface AppointmentMemberRepository extends JpaRepository<AppointmentMember, Integer> {

}
