package iks.surveytool.repositories;

import iks.surveytool.entities.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
    Optional<Survey> findSurveyByParticipationID(String participationID);

    Optional<Survey> findSurveyByAccessID(String accessId);

    List<Survey> findSurveysByOpenIsTrueAndEndDateIsAfterOrderByStartDate(LocalDateTime localDateTime);
}
