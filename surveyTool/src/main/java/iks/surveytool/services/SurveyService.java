package iks.surveytool.services;

import iks.surveytool.entities.QuestionGroup;
import iks.surveytool.entities.Survey;
import iks.surveytool.repositories.QuestionGroupRepository;
import iks.surveytool.repositories.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final QuestionGroupRepository questionGroupRepository;

    public Long addSurvey(Survey survey) {
        Survey savedSurvey = surveyRepository.save(survey);
        return savedSurvey.getId();
    }

    public void addQuestionGroupToSurvey(QuestionGroup questionGroup, Survey survey) {
        questionGroup.setSurvey(survey);
        questionGroupRepository.save(questionGroup);
    }

    public boolean validateDates(LocalDateTime startDate, LocalDateTime endDate) {
        return endDate.isAfter(startDate);
    }

    public Survey findById(Long surveyID) {
        return surveyRepository.findSurveyById(surveyID);
    }
}
