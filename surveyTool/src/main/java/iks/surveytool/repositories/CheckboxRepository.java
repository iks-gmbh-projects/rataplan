package iks.surveytool.repositories;

import iks.surveytool.entities.Checkbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckboxRepository extends JpaRepository<Checkbox, Long> {
}