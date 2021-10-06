package iks.surveytool.services;

import iks.surveytool.entities.Survey;
import iks.surveytool.repositories.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    
    public void addSurvey(Survey survey) {
        surveyRepository.save(survey);
    }
}
