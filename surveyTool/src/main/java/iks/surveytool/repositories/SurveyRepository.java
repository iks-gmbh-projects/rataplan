package iks.surveytool.repositories;

import iks.surveytool.entities.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
    Optional<Survey> findSurveyByParticipationId(String participationId);

    Optional<Survey> findSurveyByAccessId(String accessId);

    List<Survey> findSurveysByOpenAccessIsTrueAndEndDateIsAfterOrderByStartDate(LocalDateTime localDateTime);
}
