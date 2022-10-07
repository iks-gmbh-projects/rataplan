package iks.surveytool.repositories;

import iks.surveytool.entities.Survey;
import iks.surveytool.entities.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {
    boolean existsBySurveyAndUserId(Survey survey, Long userId);
    List<SurveyResponse> findAllBySurvey(Survey survey);
    Long deleteAllBySurvey(Survey survey);
    List<SurveyResponse> findAllByUserId(Long userId);
}
