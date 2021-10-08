package iks.surveytool.services;

import iks.surveytool.entities.CheckboxGroup;
import iks.surveytool.entities.Question;
import iks.surveytool.entities.QuestionGroup;
import iks.surveytool.entities.Survey;
import iks.surveytool.repositories.CheckboxGroupRepository;
import iks.surveytool.repositories.QuestionGroupRepository;
import iks.surveytool.repositories.QuestionRepository;
import iks.surveytool.repositories.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final QuestionGroupRepository questionGroupRepository;
    private final QuestionRepository questionRepository;
    private final CheckboxGroupRepository checkboxGroupRepository;

    public Long addSurvey(Survey survey) {
        Survey savedSurvey = surveyRepository.save(survey);
        return savedSurvey.getId();
    }

    public void addQuestionGroupToSurvey(QuestionGroup questionGroup, Long surveyID) {
        Survey survey = surveyRepository.findSurveyById(surveyID);
        questionGroup.setSurvey(survey);
        questionGroupRepository.save(questionGroup);
    }

    public void addQuestionToQuestionGroup(Question question, Long questionGroupID) {
        QuestionGroup questionGroup = questionGroupRepository.findQuestionGroupById(questionGroupID);
        question.setQuestionGroup(questionGroup);
        questionRepository.save(question);
    }

    public boolean validateDates(LocalDateTime startDate, LocalDateTime endDate) {
        return endDate.isAfter(startDate);
    }

    public Survey findById(Long surveyID) {
        return surveyRepository.findSurveyById(surveyID);
    }

    public void addCheckboxGroupToQuestion(CheckboxGroup checkboxGroup, Question question) {
        checkboxGroup.setQuestion(question);
        checkboxGroupRepository.save(checkboxGroup);
    }
}
