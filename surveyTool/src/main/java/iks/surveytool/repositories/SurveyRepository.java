package iks.surveytool.repositories;

import iks.surveytool.entities.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
    
}
