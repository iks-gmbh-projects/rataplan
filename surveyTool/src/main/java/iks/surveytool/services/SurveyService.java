package iks.surveytool.services;

import iks.surveytool.entities.CheckboxGroup;
import iks.surveytool.entities.Question;
import iks.surveytool.entities.QuestionGroup;
import iks.surveytool.entities.Survey;
import iks.surveytool.repositories.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;

    public Optional<Survey> findSurveyById(Long surveyID) {
        return surveyRepository.findSurveyById(surveyID);
    }

    public Optional<Survey> findSurveyByUUID(UUID uuid) {
        return surveyRepository.findSurveyByUuid(uuid);
    }

    public Optional<Survey> findSurveyByAccessId(String accessId) {
        return surveyRepository.findSurveyByAccessID(accessId);
    }

    public Long saveSurvey(Survey survey) {
        Survey surveyFixedForeignKeys = setCheckboxGroupForeignKeys(survey);
        Survey savedSurvey = surveyRepository.save(surveyFixedForeignKeys);
        generateAccessID(savedSurvey);
        generateUUID(savedSurvey);
        savedSurvey = surveyRepository.save(survey);
        return savedSurvey.getId();
    }

    private Survey setCheckboxGroupForeignKeys(Survey survey) {
        for (QuestionGroup questionGroup : survey.getQuestionGroups()) {
            for (Question question : questionGroup.getQuestions()) {
                if (question.isHasCheckbox()) {
                    question.getCheckboxGroup().setQuestion(question);
                }
            }
        }
        return survey;
    }

    // Temporary:
    public void generateAccessID(Survey survey) {
        Random random = new Random();
        String accessID = "";
        accessID += survey.getStartDate().getYear() + "-" + random.nextInt(10) + "-" + survey.getId();
        survey.setAccessID(accessID);
    }

    private void generateUUID(Survey survey) {
        UUID uuid = UUID.randomUUID();
        survey.setUuid(uuid);
    }
    
    public List<String> checkIfAnythingEmpty(Survey survey) {
        List<String> errorMessages = new ArrayList<>();
        checkQuestionGroups(survey, errorMessages);
        return errorMessages;
    }

    private void checkQuestionGroups(Survey survey, List<String> errorMessages) {
        List<QuestionGroup> questionGroups = survey.getQuestionGroups();
        if (questionGroups == null) {
            errorMessages.add("Eine Umfrage muss aus mind. einem Frageblock bestehen.");
        } else {
            for (QuestionGroup questionGroup : questionGroups) {
                List<Question> questions = questionGroup.getQuestions();
                if (questions == null) {
                    errorMessages.add("Der Frageblock '" + questionGroup.getTitle() + "' enthält noch keine Frage.");
                } else {
                    checkQuestions(questionGroup, errorMessages);
                }
            }
        }
    }

    private void checkQuestions(QuestionGroup questionGroup, List<String> errorMessages) {
        List<Question> questions = questionGroup.getQuestions();
        for (Question question : questions) {
            if (question.isHasCheckbox()) {
                CheckboxGroup checkboxGroup = question.getCheckboxGroup();
                if (checkboxGroup.getCheckboxes() == null) {
                    errorMessages.add("Frageblock: '" + questionGroup.getTitle() + "', Frage: '" + question.getText()
                            + "'\nDiese Frage enthält noch keine Antwortmöglichkeiten.");
                } else {
                    int numberOfCheckboxes = checkboxGroup.getCheckboxes().size();
                    if (!checkboxGroup.isMultipleSelect() && numberOfCheckboxes < 2) {
                        errorMessages.add("Frageblock: '" + questionGroup.getTitle() + "', Frage: '" + question.getText()
                                + "'\nBei einer Frage mit Auswahlmöglichkeiten müssen mind. 2 Antworten gegeben sein.");
                    } else if (checkboxGroup.isMultipleSelect() &&
                            numberOfCheckboxes < checkboxGroup.getMaxSelect()) {
                        errorMessages.add("Frageblock: '" + questionGroup.getTitle() + "', Frage: '" + question.getText()
                                + "'\nBei einer Frage mit Auswahlmöglichkeiten müssen mind. 2 Antworten gegeben sein, " +
                                "bzw. mind. so viele Antworten wie bei der Mehrfachauswahl maximal erlaubt sind.");
                    }
                }
            }
        }
    }
}
