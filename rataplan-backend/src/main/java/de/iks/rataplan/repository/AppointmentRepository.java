package de.iks.rataplan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.iks.rataplan.domain.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

}
