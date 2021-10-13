package iks.surveytool.services;

import iks.surveytool.entities.*;
import iks.surveytool.repositories.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;

    public Survey findSurveyById(Long surveyID) {
        return surveyRepository.findSurveyById(surveyID);
    }

    public void addSurvey(Survey survey) {
        surveyRepository.save(survey);
    }

    public void addQuestionGroupToSurvey(Survey survey, QuestionGroup questionGroup) {
        if (survey.getQuestionGroups() == null) {
            survey.setQuestionGroups(new ArrayList<>());
        }
        survey.getQuestionGroups().add(questionGroup);

        questionGroup.setSurvey(survey);
    }

    public void addQuestionToQuestionGroup(Survey survey,
                                           int questionGroupIndex,
                                           Question question,
                                           CheckboxGroup checkboxGroup) {
        if (question.isHasCheckbox()) {
            question.setCheckboxGroup(checkboxGroup);
            checkboxGroup.setQuestion(question);
        }

        QuestionGroup questionGroup = survey.getQuestionGroups().get(questionGroupIndex);
        if (questionGroup.getQuestions() == null) {
            questionGroup.setQuestions(new ArrayList<>());
        }
        questionGroup.getQuestions().add(question);

        question.setQuestionGroup(questionGroup);
    }

    public void addCheckboxToQuestion(Survey survey, int questionGroupIndex, int questionIndex, Checkbox checkbox) {
        CheckboxGroup checkboxGroup = survey.getQuestionGroups()
                .get(questionGroupIndex)
                .getQuestions()
                .get(questionIndex)
                .getCheckboxGroup();

        if (checkboxGroup.getCheckboxes() == null) {
            checkboxGroup.setCheckboxes(new ArrayList<>());
        }
        checkboxGroup.getCheckboxes().add(checkbox);

        checkbox.setCheckboxGroup(checkboxGroup);
    }

    public boolean validateDates(LocalDateTime startDate, LocalDateTime endDate) {
        return endDate.isAfter(startDate);
    }

}
