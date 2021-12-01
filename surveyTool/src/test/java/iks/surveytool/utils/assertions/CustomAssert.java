package iks.surveytool.utils.assertions;

import iks.surveytool.dtos.*;
import iks.surveytool.entities.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomAssert {
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
}
