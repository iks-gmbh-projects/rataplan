package iks.surveytool.repositories;

import iks.surveytool.entities.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
    Optional<Survey> findSurveyById(Long id);

    Optional<Survey> findSurveyByUuid(UUID uuid);

    Optional<Survey> findSurveyByAccessID(String accessId);

}
