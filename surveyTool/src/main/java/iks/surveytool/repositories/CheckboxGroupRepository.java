package iks.surveytool.repositories;

import iks.surveytool.entities.CheckboxGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckboxGroupRepository extends JpaRepository<CheckboxGroup, Long> {
    
}
