package iks.surveytool.services;

import iks.surveytool.entities.*;
import iks.surveytool.repositories.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;

    public Optional<Survey> findSurveyById(Long surveyID) {
        return surveyRepository.findSurveyById(surveyID);
    }

    public Long addSurvey(Survey survey) {
        Survey savedSurvey = surveyRepository.save(survey);
        generateAccessID(savedSurvey);
        // TODO: Create link to invite users
        // generateLink(savedSurvey);
        savedSurvey = surveyRepository.save(survey);
        return savedSurvey.getId();
    }

    // Temporary:
    public void generateAccessID(Survey survey) {
        Random random = new Random();
        String accessID = "";
        accessID += survey.getStartDate().getYear() + "-" + random.nextInt(10) + "-" + survey.getId();
        survey.setAccessID(accessID);
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

    public boolean startDateBeforeEndDate(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate.isBefore(endDate);
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
