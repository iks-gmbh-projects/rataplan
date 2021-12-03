package iks.surveytool.utils.assertions;

import iks.surveytool.dtos.*;
import iks.surveytool.entities.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MappingAssertions {
    public static void assertCompleteSurveyDTO(CompleteSurveyDTO surveyDTO, Survey survey) {
        assertEquals(surveyDTO.getId(), survey.getId());
        assertEquals(surveyDTO.getName(), survey.getName());
        assertEquals(surveyDTO.getDescription(), survey.getDescription());
        assertEquals(surveyDTO.getStartDate(), survey.getStartDate());
        assertEquals(surveyDTO.getEndDate(), survey.getEndDate());
        assertEquals(surveyDTO.isOpenAccess(), survey.isOpenAccess());
        assertEquals(surveyDTO.getAccessId(), survey.getAccessId());
        assertEquals(surveyDTO.getParticipationId(), survey.getParticipationId());

        assertEquals(surveyDTO.getUserId(), survey.getUser().getId());
        assertEquals(surveyDTO.getUserName(), survey.getUser().getName());

        List<QuestionGroupDTO> questionGroupDTOs = surveyDTO.getQuestionGroups();
        List<QuestionGroup> questionGroups = survey.getQuestionGroups();
        for (int i = 0; i < questionGroups.size(); i++) {
            assertQuestionGroupDTO(questionGroupDTOs.get(i), questionGroups.get(i));
        }
    }

    private static void assertQuestionGroupDTO(QuestionGroupDTO questionGroupDTO, QuestionGroup questionGroup) {
        assertEquals(questionGroupDTO.getId(), questionGroup.getId());
        assertEquals(questionGroupDTO.getTitle(), questionGroup.getTitle());

        List<QuestionDTO> questionDTOs = questionGroupDTO.getQuestions();
        List<Question> questions = questionGroup.getQuestions();
        for (int i = 0; i < questions.size(); i++) {
            assertQuestionDTO(questionDTOs.get(i), questions.get(i));
        }
    }

    private static void assertQuestionDTO(QuestionDTO questionDTO, Question question) {
        assertEquals(questionDTO.getId(), question.getId());
        assertEquals(questionDTO.getText(), question.getText());
        assertEquals(questionDTO.isRequired(), question.isRequired());
        assertEquals(questionDTO.isHasCheckbox(), question.isHasCheckbox());

        if (question.isHasCheckbox()) {
            CheckboxGroupDTO checkboxGroupDTO = questionDTO.getCheckboxGroup();
            CheckboxGroup checkboxGroup = question.getCheckboxGroup();
            assertCheckboxGroupDTO(checkboxGroupDTO, checkboxGroup);
        }
    }

    private static void assertCheckboxGroupDTO(CheckboxGroupDTO checkboxGroupDTO, CheckboxGroup checkboxGroup) {
        assertEquals(checkboxGroupDTO.getId(), checkboxGroup.getId());
        assertEquals(checkboxGroupDTO.isMultipleSelect(), checkboxGroup.isMultipleSelect());
        assertEquals(checkboxGroupDTO.getMinSelect(), checkboxGroup.getMinSelect());
        assertEquals(checkboxGroupDTO.getMaxSelect(), checkboxGroup.getMaxSelect());

        List<CheckboxDTO> checkboxDTOs = checkboxGroupDTO.getCheckboxes();
        List<Checkbox> checkboxes = checkboxGroup.getCheckboxes();
        for (int i = 0; i < checkboxes.size(); i++) {
            assertCheckboxDTO(checkboxDTOs.get(i), checkboxes.get(i));
        }
    }

    private static void assertCheckboxDTO(CheckboxDTO checkboxDTO, Checkbox checkbox) {
        assertEquals(checkboxDTO.getText(), checkbox.getText());
        assertEquals(checkboxDTO.isHasTextField(), checkbox.isHasTextField());
    }

    public static void assertSurvey(Survey survey, CompleteSurveyDTO surveyDTO) {
        assertEquals(survey.getId(), surveyDTO.getId());
        assertEquals(survey.getName(), surveyDTO.getName());
        assertEquals(survey.getDescription(), surveyDTO.getDescription());
        assertEquals(survey.getStartDate(), surveyDTO.getStartDate());
        assertEquals(survey.getEndDate(), surveyDTO.getEndDate());
        assertEquals(survey.isOpenAccess(), surveyDTO.isOpenAccess());
        assertEquals(survey.getAccessId(), surveyDTO.getAccessId());
        assertEquals(survey.getParticipationId(), surveyDTO.getParticipationId());

        assertEquals(survey.getUser().getId(), surveyDTO.getUserId());
        assertEquals(survey.getUser().getName(), surveyDTO.getUserName());

        List<QuestionGroup> questionGroups = survey.getQuestionGroups();
        List<QuestionGroupDTO> questionGroupDTOs = surveyDTO.getQuestionGroups();
        for (int i = 0; i < questionGroups.size(); i++) {
            assertQuestionGroup(questionGroups.get(i), questionGroupDTOs.get(i));
        }
    }

    private static void assertQuestionGroup(QuestionGroup questionGroup, QuestionGroupDTO questionGroupDTO) {
        assertEquals(questionGroup.getId(), questionGroupDTO.getId());
        assertEquals(questionGroup.getTitle(), questionGroupDTO.getTitle());

        List<Question> questions = questionGroup.getQuestions();
        List<QuestionDTO> questionDTOs = questionGroupDTO.getQuestions();
        for (int i = 0; i < questionDTOs.size(); i++) {
            assertQuestion(questions.get(i), questionDTOs.get(i));
        }
    }

    private static void assertQuestion(Question question, QuestionDTO questionDTO) {
        assertEquals(question.getId(), questionDTO.getId());
        assertEquals(question.getText(), questionDTO.getText());
        assertEquals(question.isRequired(), questionDTO.isRequired());
        assertEquals(question.isHasCheckbox(), questionDTO.isHasCheckbox());

        if (questionDTO.isHasCheckbox()) {
            CheckboxGroup checkboxGroup = question.getCheckboxGroup();
            CheckboxGroupDTO checkboxGroupDTO = questionDTO.getCheckboxGroup();
            assertCheckboxGroup(checkboxGroup, checkboxGroupDTO);
        }
    }

    private static void assertCheckboxGroup(CheckboxGroup checkboxGroup, CheckboxGroupDTO checkboxGroupDTO) {
        assertEquals(checkboxGroup.getId(), checkboxGroupDTO.getId());
        assertEquals(checkboxGroup.isMultipleSelect(), checkboxGroupDTO.isMultipleSelect());
        assertEquals(checkboxGroup.getMinSelect(), checkboxGroupDTO.getMinSelect());
        assertEquals(checkboxGroup.getMaxSelect(), checkboxGroupDTO.getMaxSelect());

        List<Checkbox> checkboxes = checkboxGroup.getCheckboxes();
        List<CheckboxDTO> checkboxDTOs = checkboxGroupDTO.getCheckboxes();
        for (int i = 0; i < checkboxDTOs.size(); i++) {
            assertCheckbox(checkboxes.get(i), checkboxDTOs.get(i));
        }
    }

    private static void assertCheckbox(Checkbox checkbox, CheckboxDTO checkboxDTO) {
        assertEquals(checkbox.getText(), checkboxDTO.getText());
        assertEquals(checkbox.isHasTextField(), checkboxDTO.isHasTextField());
    }

    public static void assertAnswerDTOs(List<AnswerDTO> answerDTOs, List<Answer> answers) {
        for (int i = 0; i < answers.size(); i++) {
            assertAnswerDTO(answerDTOs.get(i), answers.get(i));
        }
    }

    private static void assertAnswerDTO(AnswerDTO answerDTO, Answer answer) {
        assertEquals(answerDTO.getId(), answer.getId());
        assertEquals(answerDTO.getText(), answer.getText());

        assertEquals(answerDTO.getUserId(), answer.getUser().getId());
        assertEquals(answerDTO.getUserName(), answer.getUser().getName());

        if (answer.getCheckbox() != null) {
            assertEquals(answerDTO.getCheckboxId(), answer.getCheckbox().getId());
        }
        assertEquals(answerDTO.getQuestionId(), answer.getQuestion().getId());
    }

    public static void assertAnswers(List<Answer> answers, List<AnswerDTO> answerDTOs) {
        for (int i = 0; i < answerDTOs.size(); i++) {
            assertAnswer(answers.get(i), answerDTOs.get(i));
        }
    }

    private static void assertAnswer(Answer answer, AnswerDTO answerDTO) {
        assertEquals(answer.getId(), answerDTO.getId());
        assertEquals(answer.getText(), answerDTO.getText());

        assertEquals(answer.getUser().getId(), answerDTO.getUserId());
        assertEquals(answer.getUser().getName(), answerDTO.getUserName());

        if (answerDTO.getCheckboxId() != null) {
            assertEquals(answer.getCheckbox().getId(), answerDTO.getCheckboxId());
        }
        assertEquals(answer.getQuestion().getId(), answerDTO.getQuestionId());
    }

    public static void assertUserDTO(UserDTO userDTO, User user) {
        assertEquals(userDTO.getId(), user.getId());
        assertEquals(userDTO.getName(), user.getName());
    }

    public static void assertUser(User user, UserDTO userDTO) {
        assertEquals(user.getId(), userDTO.getId());
        assertEquals(user.getName(), userDTO.getName());
    }
}
