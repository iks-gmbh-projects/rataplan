package iks.surveytool.services;

import iks.surveytool.entities.Survey;
import iks.surveytool.repositories.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;

    public void addSurvey(Survey survey, LocalDateTime startDate, LocalDateTime endDate) {
        survey.setStartDate(startDate);
        survey.setEndDate(endDate);
        surveyRepository.save(survey);
    }
}
