package iks.surveytool.utils.assertions;

import iks.surveytool.dtos.*;
import iks.surveytool.entities.*;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class MappingAssertions {
    public static void assertCompleteSurveyDTO(CompleteSurveyDTO surveyDTO, Survey survey) {
        assertEquals(surveyDTO.getId(), survey.getId());
        assertEquals(surveyDTO.getName(), survey.getName());
        assertEquals(surveyDTO.getDescription(), survey.getDescription());
        assertEquals(surveyDTO.getStartDate(), survey.getStartDate().toInstant());
        assertEquals(surveyDTO.getEndDate(), survey.getEndDate().toInstant());
        assertEquals(surveyDTO.isOpenAccess(), survey.isOpenAccess());
        assertEquals(surveyDTO.isAnonymousParticipation(), survey.isAnonymousParticipation());
        assertEquals(surveyDTO.getAccessId(), survey.getAccessId());
        assertEquals(surveyDTO.getParticipationId(), survey.getParticipationId());

        assertEquals(surveyDTO.getUserId(), survey.getUserId());

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

    private static Instant toInstant(ZonedDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.toInstant();
    }

    public static void assertSurvey(Survey survey, CompleteSurveyDTO surveyDTO) {
        assertEquals(survey.getId(), surveyDTO.getId());
        assertEquals(survey.getName(), surveyDTO.getName());
        assertEquals(survey.getDescription(), surveyDTO.getDescription());
        assertEquals(toInstant(survey.getStartDate()), surveyDTO.getStartDate());
        assertEquals(toInstant(survey.getEndDate()), surveyDTO.getEndDate());
        assertEquals(survey.isOpenAccess(), surveyDTO.isOpenAccess());
        assertEquals(survey.isAnonymousParticipation(), surveyDTO.isAnonymousParticipation());
        assertEquals(survey.getAccessId(), surveyDTO.getAccessId());
        assertEquals(survey.getParticipationId(), surveyDTO.getParticipationId());

        assertEquals(survey.getUserId(), surveyDTO.getUserId());

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

    private static <U, V> V passNull(Function<U, V> mapper, U u) {
        if (u == null) return null;
        return mapper.apply(u);
    }

    public static void assertSurveyResponseDTO(SurveyResponseDTO responseDTO, SurveyResponse response) {
        assertEquals(response.getId(), responseDTO.getId());
        assertEquals(passNull(Survey::getId, response.getSurvey()), responseDTO.getSurveyId());
        assertEquals(response.getUserId(), responseDTO.getUserId());
        List<AnswerDTO> answerDTOs = responseDTO.getAnswers()
                .values()
                .stream()
                .sorted(Comparator.comparing(AnswerDTO::getId))
                .collect(Collectors.toList());
        List<Answer> answers = response.getAnswers()
                .stream()
                .sorted(Comparator.comparing(Answer::getId))
                .collect(Collectors.toList());
        assertEquals(answerDTOs.size(), answers.size());
        Iterator<Answer> answerIterator = answers.iterator();
        for (AnswerDTO answerDTO : answerDTOs) {
            assertAnswerDTO(answerDTO, answerIterator.next());
        }
    }

    private static void assertAnswerDTO(AnswerDTO answerDTO, Answer answer) {
        assertEquals(answerDTO.getId(), answer.getId());
        assertEquals(answerDTO.getText(), answer.getText());

        if (answer.getCheckboxes() != null) {
            Set<Long> ids = new HashSet<>(answer.getCheckboxes().size());
            for (Checkbox checkbox : answer.getCheckboxes()) {
                ids.add(checkbox.getId());
                assertEquals(Boolean.TRUE, answerDTO.getCheckboxes().get(checkbox.getId()));
            }
            for (Map.Entry<Long, Boolean> entry : answerDTO.getCheckboxes().entrySet()) {
                if (ids.contains(entry.getKey())) continue;
                assertNotEquals(Boolean.TRUE, entry.getValue());
            }
        }
    }

    public static void assertSurveyResponse(SurveyResponse response, SurveyResponseDTO responseDTO) {
        assertEquals(responseDTO.getId(), response.getId());
        assertEquals(responseDTO.getSurveyId(), passNull(Survey::getId, response.getSurvey()));
        assertEquals(responseDTO.getUserId(), response.getUserId());
        List<AnswerDTO> answerDTOs = responseDTO.getAnswers()
                .values()
                .stream()
                .sorted(Comparator.comparing(AnswerDTO::getId))
                .collect(Collectors.toList());
        List<Answer> answers = response.getAnswers()
                .stream()
                .sorted(Comparator.comparing(Answer::getId))
                .collect(Collectors.toList());
        assertEquals(answerDTOs.size(), answers.size());
        Iterator<AnswerDTO> answerDTOIterator = answerDTOs.iterator();
        for (Answer answer : answers) {
            assertAnswer(answer, answerDTOIterator.next());
        }
    }

    private static void assertAnswer(Answer answer, AnswerDTO answerDTO) {
        assertEquals(answer.getId(), answerDTO.getId());
        assertEquals(answer.getText(), answerDTO.getText());

        if (answerDTO.getCheckboxes() != null) {
            Set<Long> ids = new HashSet<>(answer.getCheckboxes().size());
            for (Checkbox checkbox : answer.getCheckboxes()) {
                ids.add(checkbox.getId());
                assertEquals(Boolean.TRUE, answerDTO.getCheckboxes().get(checkbox.getId()));
            }
            for (Map.Entry<Long, Boolean> entry : answerDTO.getCheckboxes().entrySet()) {
                if (ids.contains(entry.getKey())) continue;
                assertNotEquals(Boolean.TRUE, entry.getValue());
            }
        }
    }
}
