package iks.surveytool.services;

import iks.surveytool.builder.*;
import iks.surveytool.entities.*;
import iks.surveytool.repositories.SurveyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testing SurveyService")
class SurveyServiceTest {

    @Mock
    private SurveyRepository surveyRepository;

    @InjectMocks
    private SurveyService surveyService;

    @Test
    @DisplayName("Adding Survey")
    void addSurvey() {
        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Test Umfrage");
        when(surveyRepository.save(survey)).thenReturn(survey);

        surveyService.saveSurvey(survey);

        verify(surveyRepository, times(1)).save(survey);
    }

    @Test
    @DisplayName("Failed validation - Survey missing QuestionGroups")
    void surveyIsMissingQuestionGroups_Failed() {
        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Survey without QuestionGroup");

        boolean somethingIsMissing = surveyService.checkIfAnythingEmpty(survey);

        assertTrue(somethingIsMissing);
    }

    @Test
    @DisplayName("Failed validation - QuestionGroup missing Questions")
    void questionGroupIsMissingQuestion_failed() {
        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, false);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Question");
        questionGroupWithQuestion.setQuestions(List.of(question));
        QuestionGroup questionGroupWithoutQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(2L, "QuestionGroup without Question");

        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Survey with empty QuestionGroup");
        survey.setQuestionGroups(List.of(questionGroupWithQuestion, questionGroupWithoutQuestion));

        boolean somethingIsMissing = surveyService.checkIfAnythingEmpty(survey);

        assertTrue(somethingIsMissing);
    }

    @Test
    @DisplayName("Failed validation - No Checkboxes - No multipleSelect")
    void questionNoCheckboxes_failed() {
        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .createCheckboxGroup(1L, false, 0, 0);

        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, true);
        question.setCheckboxGroup(checkboxGroup);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Question");
        questionGroupWithQuestion.setQuestions(List.of(question));

        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Survey with no checkboxes for question");
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        boolean somethingIsMissing = surveyService.checkIfAnythingEmpty(survey);

        assertTrue(somethingIsMissing);
    }

    @Test
    @DisplayName("Failed validation - Not enough Checkboxes - No multipleSelect")
    void questionNotEnoughCheckboxes_failed() {
        Checkbox onlyCheckbox = new CheckboxBuilder()
                .createCheckbox(1L, "First Test Checkbox", false);

        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .createCheckboxGroup(1L, false, 0, 0);
        checkboxGroup.setCheckboxes(List.of(onlyCheckbox));

        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, true);
        question.setCheckboxGroup(checkboxGroup);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Question");
        questionGroupWithQuestion.setQuestions(List.of(question));

        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Survey with not enough checkboxes for question");
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        boolean somethingIsMissing = surveyService.checkIfAnythingEmpty(survey);

        assertTrue(somethingIsMissing);
    }

    @Test
    @DisplayName("Failed validation - Not enough Checkboxes - With multipleSelect (max: 4)")
    void questionNotEnoughCheckboxes_multipleSelect_failed() {
        Checkbox firstCheckbox = new CheckboxBuilder()
                .createCheckbox(1L, "First Test Checkbox", false);
        Checkbox secondCheckbox = new CheckboxBuilder()
                .createCheckbox(2L, "Second Test Checkbox", true);
        Checkbox thirdCheckbox = new CheckboxBuilder()
                .createCheckbox(3L, "Third Test Checkbox", true);

        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .createCheckboxGroup(1L, true, 2, 4);
        checkboxGroup.setCheckboxes(List.of(firstCheckbox, secondCheckbox, thirdCheckbox));

        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, true);
        question.setCheckboxGroup(checkboxGroup);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Question");
        questionGroupWithQuestion.setQuestions(List.of(question));

        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Survey with not enough checkboxes for question");
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        boolean somethingIsMissing = surveyService.checkIfAnythingEmpty(survey);

        assertTrue(somethingIsMissing);
    }

    @Test
    @DisplayName("Successful validation - saving complete survey")
    void surveyIsComplete_Successful() {
        Checkbox firstCheckbox = new CheckboxBuilder()
                .createCheckbox(1L, "First Test Checkbox", false);
        Checkbox secondCheckbox = new CheckboxBuilder()
                .createCheckbox(2L, "Second Test Checkbox", true);
        Checkbox thirdCheckbox = new CheckboxBuilder()
                .createCheckbox(3L, "Third Test Checkbox", true);
        Checkbox fourthCheckbox = new CheckboxBuilder()
                .createCheckbox(4L, "Fourth Test Checkbox", false);

        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .createCheckboxGroup(1L, true, 1, 3);
        checkboxGroup.setCheckboxes(List.of(firstCheckbox, secondCheckbox, thirdCheckbox, fourthCheckbox));

        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, true);
        question.setCheckboxGroup(checkboxGroup);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Question");
        questionGroupWithQuestion.setQuestions(List.of(question));

        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Complete Survey");
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        boolean somethingIsMissing = surveyService.checkIfAnythingEmpty(survey);

        assertFalse(somethingIsMissing);
    }
}