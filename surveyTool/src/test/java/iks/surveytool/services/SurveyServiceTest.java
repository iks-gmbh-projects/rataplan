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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
        Survey survey = new SurveyBuilder().build();
        when(surveyRepository.save(survey)).thenReturn(survey);

        surveyService.addSurvey(survey);

        verify(surveyRepository, times(1)).save(survey);
    }

    @Test
    @DisplayName("Adding QuestionGroup to Survey")
    void addQuestionGroupToSurvey() {
        Survey survey = new SurveyBuilder()
                .setQuestionGroups(null)
                .build();
        QuestionGroup questionGroup = new QuestionGroupBuilder()
                .setTitle("Add test group")
                .setSurvey(null)
                .build();

        surveyService.addQuestionGroupToSurvey(survey, questionGroup);

        assertEquals(questionGroup, survey.getQuestionGroups().get(0));
    }

    @Test
    @DisplayName("Adding multiple QuestionGroups to Survey")
    void addMultipleQuestionGroupsToSurvey() {
        Survey survey = new SurveyBuilder()
                .setQuestionGroups(null)
                .build();
        QuestionGroup questionGroup1 = new QuestionGroupBuilder()
                .setTitle("Add testGroup1")
                .setSurvey(null)
                .build();
        QuestionGroup questionGroup2 = new QuestionGroupBuilder()
                .setTitle("Add testGroup2")
                .setSurvey(null)
                .build();

        surveyService.addQuestionGroupToSurvey(survey, questionGroup1);
        surveyService.addQuestionGroupToSurvey(survey, questionGroup2);

        assertEquals(questionGroup1, survey.getQuestionGroups().get(0));
        assertEquals(questionGroup2, survey.getQuestionGroups().get(1));
    }

    @Test
    @DisplayName("Adding Question to QuestionGroup - No Checkbox")
    void addQuestionToQuestionGroup_NoCheckbox() {
        Survey survey = new SurveyBuilder()
                .addQuestionGroup("Test group for adding questions")
                .build();
        Question question = new QuestionBuilder()
                .setText("Add Question")
                .setHasCheckbox(false)
                .setCheckboxGroup(null)
                .build();
        CheckboxGroup checkboxGroup = new CheckboxGroup();

        surveyService.addQuestionToQuestionGroup(survey, 0, question, checkboxGroup);

        assertEquals(question, survey.getQuestionGroups().get(0).getQuestions().get(0));
        assertNull(survey.getQuestionGroups().get(0).getQuestions().get(0).getCheckboxGroup());
    }

    @Test
    @DisplayName("Adding multiple Questions to QuestionGroup")
    void addMultipleQuestionsToQuestionGroup() {
        Survey survey = new SurveyBuilder()
                .addQuestionGroup("Test group for adding questions")
                .build();
        Question firstQuestion = new QuestionBuilder()
                .setText("Add first Question")
                .setHasCheckbox(false)
                .setCheckboxGroup(null)
                .build();
        Question secondQuestion = new QuestionBuilder()
                .setText("Add second Question")
                .setHasCheckbox(true)
                .setCheckboxGroup(null)
                .build();
        CheckboxGroup firstCheckboxGroup = new CheckboxGroup();
        CheckboxGroup secondCheckboxGroup = new CheckboxGroup();

        surveyService.addQuestionToQuestionGroup(survey, 0, firstQuestion, firstCheckboxGroup);
        surveyService.addQuestionToQuestionGroup(survey, 0, secondQuestion, secondCheckboxGroup);

        assertEquals(firstQuestion, survey.getQuestionGroups().get(0).getQuestions().get(0));
        assertNull(survey.getQuestionGroups().get(0).getQuestions().get(0).getCheckboxGroup());
        assertEquals(secondQuestion, survey.getQuestionGroups().get(0).getQuestions().get(1));
        assertNotNull(survey.getQuestionGroups().get(0).getQuestions().get(1).getCheckboxGroup());
    }

    @Test
    @DisplayName("Adding Question to second QuestionGroup - No Checkbox")
    void addQuestionToSecondQuestionGroup_NoCheckbox() {
        Survey survey = new SurveyBuilder()
                .addQuestionGroup("First test group for adding questions")
                .addQuestionGroup("Second test group for adding questions")
                .build();
        Question question = new QuestionBuilder()
                .setText("Add Question")
                .setHasCheckbox(false)
                .setCheckboxGroup(null)
                .build();
        CheckboxGroup checkboxGroup = new CheckboxGroup();

        surveyService.addQuestionToQuestionGroup(survey, 1, question, checkboxGroup);

        assertEquals(question, survey.getQuestionGroups().get(1).getQuestions().get(0));
        assertNull(survey.getQuestionGroups().get(1).getQuestions().get(0).getCheckboxGroup());
    }

    @Test
    @DisplayName("Adding Checkbox to Question")
    void addCheckboxToQuestion() {
        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .setMultipleSelect(false)
                .setCheckboxes(null)
                .build();
        Question question = new QuestionBuilder()
                .setText("Add Question")
                .setHasCheckbox(true)
                .setCheckboxGroup(checkboxGroup)
                .build();
        Survey survey = new SurveyBuilder()
                .addQuestionGroup("Test group for adding checkboxes to question")
                .addQuestionToGroup(question, 0)
                .build();
        Checkbox checkbox = new CheckboxBuilder()
                .setText("Test Checkbox")
                .build();

        surveyService.addCheckboxToQuestion(survey, 0, 0, checkbox);

        assertEquals(checkbox,
                survey.getQuestionGroups().get(0).getQuestions().get(0).getCheckboxGroup().getCheckboxes().get(0));
    }

    @Test
    @DisplayName("Adding multiple Checkboxes to Question")
    void addMultipleCheckboxesToQuestion() {
        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .setMultipleSelect(false)
                .setCheckboxes(null)
                .build();
        Question question = new QuestionBuilder()
                .setText("Add Question")
                .setHasCheckbox(true)
                .setCheckboxGroup(checkboxGroup)
                .build();
        Survey survey = new SurveyBuilder()
                .addQuestionGroup("Test group for adding checkboxes to question")
                .addQuestionToGroup(question, 0)
                .build();
        Checkbox firstCheckbox = new CheckboxBuilder()
                .setText("First Test Checkbox")
                .build();
        Checkbox secondCheckbox = new CheckboxBuilder()
                .setText("Second Test Checkbox")
                .setHasTextField(true)
                .build();

        surveyService.addCheckboxToQuestion(survey, 0, 0, firstCheckbox);
        surveyService.addCheckboxToQuestion(survey, 0, 0, secondCheckbox);

        assertEquals(firstCheckbox,
                survey.getQuestionGroups().get(0).getQuestions().get(0).getCheckboxGroup().getCheckboxes().get(0));
        assertEquals(secondCheckbox,
                survey.getQuestionGroups().get(0).getQuestions().get(0).getCheckboxGroup().getCheckboxes().get(1));
    }

    @Test
    @DisplayName("Successful validation - startDate before endDate")
    void startDateBeforeEndDate_Successful() {
        LocalDateTime startDate = LocalDateTime.of(3000, 9, 1, 13, 0);
        LocalDateTime endDate = LocalDateTime.of(3000, 10, 1, 13, 0);

        boolean result = surveyService.startDateBeforeEndDate(startDate, endDate);

        assertTrue(result);
    }

    @Test
    @DisplayName("Failed validation - startDate before endDate")
    void startDateBeforeEndDate_Failed() {
        LocalDateTime startDate = LocalDateTime.of(2021, 10, 31, 13, 0);
        LocalDateTime endDate = LocalDateTime.of(2021, 10, 1, 13, 0);

        boolean result = surveyService.startDateBeforeEndDate(startDate, endDate);

        assertFalse(result);
    }

    @Test
    @DisplayName("Failed validation - Survey missing QuestionGroups")
    void surveyIsMissingQuestionGroups_Failed() {
        Survey survey = new SurveyBuilder()
                .setName("Survey without QuestionGroup")
                .setQuestionGroups(null)
                .build();

        List<String> errorMessages = surveyService.checkIfAnythingEmpty(survey);

        assertFalse(errorMessages.isEmpty());
    }

    @Test
    @DisplayName("Failed validation - QuestionGroup missing Questions")
    void questionGroupIsMissingQuestion_failed() {
        Question question = new QuestionBuilder()
                .setText("Test Question")
                .setHasCheckbox(false)
                .build();
        Survey survey = new SurveyBuilder()
                .setName("Survey with empty QuestionGroup")
                .addQuestionGroup("QuestionGroup with Question")
                .addQuestionToGroup(question, 0)
                .addQuestionGroup("QuestionGroup without Question")
                .build();

        List<String> errorMessages = surveyService.checkIfAnythingEmpty(survey);

        assertFalse(errorMessages.isEmpty());
    }

    @Test
    @DisplayName("Failed validation - No Checkboxes - No multipleSelect")
    void questionNoCheckboxes_failed() {
        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .setMultipleSelect(false)
                .setCheckboxes(null)
                .build();
        Question question = new QuestionBuilder()
                .setText("Test Question")
                .setHasCheckbox(true)
                .setCheckboxGroup(checkboxGroup)
                .build();
        Survey survey = new SurveyBuilder()
                .setName("Survey with not enough checkboxes for question")
                .addQuestionGroup("QuestionGroup with Question")
                .addQuestionToGroup(question, 0)
                .build();

        List<String> errorMessages = surveyService.checkIfAnythingEmpty(survey);

        assertFalse(errorMessages.isEmpty());
    }

    @Test
    @DisplayName("Failed validation - Not enough Checkboxes - No multipleSelect")
    void questionNotEnoughCheckboxes_failed() {
        Checkbox onlyCheckbox = new CheckboxBuilder()
                .setText("First Test Checkbox")
                .build();
        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .setMultipleSelect(false)
                .setCheckboxes(List.of(onlyCheckbox))
                .build();
        Question question = new QuestionBuilder()
                .setText("Test Question")
                .setHasCheckbox(true)
                .setCheckboxGroup(checkboxGroup)
                .build();
        Survey survey = new SurveyBuilder()
                .setName("Survey with not enough checkboxes for question")
                .addQuestionGroup("QuestionGroup with Question")
                .addQuestionToGroup(question, 0)
                .build();

        List<String> errorMessages = surveyService.checkIfAnythingEmpty(survey);

        assertFalse(errorMessages.isEmpty());
    }

    @Test
    @DisplayName("Failed validation - Not enough Checkboxes - With multipleSelect (max: 4)")
    void questionNotEnoughCheckboxes_multipleSelect_failed() {
        Checkbox firstCheckbox = new CheckboxBuilder()
                .setText("First Test Checkbox")
                .build();
        Checkbox secondCheckbox = new CheckboxBuilder()
                .setText("Second Test Checkbox")
                .setHasTextField(true)
                .build();
        Checkbox thirdCheckbox = new CheckboxBuilder()
                .setText("Third Test Checkbox")
                .setHasTextField(true)
                .build();
        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .setMultipleSelect(true)
                .setMinSelect(2)
                .setMaxSelect(4)
                .setCheckboxes(List.of(firstCheckbox, secondCheckbox, thirdCheckbox))
                .build();
        Question question = new QuestionBuilder()
                .setText("Test Question")
                .setHasCheckbox(true)
                .setCheckboxGroup(checkboxGroup)
                .build();
        Survey survey = new SurveyBuilder()
                .setName("Survey with not enough checkboxes for question")
                .addQuestionGroup("QuestionGroup with Question")
                .addQuestionToGroup(question, 0)
                .build();

        List<String> errorMessages = surveyService.checkIfAnythingEmpty(survey);

        assertFalse(errorMessages.isEmpty());
    }

    @Test
    @DisplayName("Successful validation - saving complete survey")
    void surveyIsComplete_Succesful() {
        Checkbox firstCheckbox = new CheckboxBuilder()
                .setText("First Test Checkbox")
                .build();
        Checkbox secondCheckbox = new CheckboxBuilder()
                .setText("Second Test Checkbox")
                .setHasTextField(true)
                .build();
        Checkbox thirdCheckbox = new CheckboxBuilder()
                .setText("Third Test Checkbox")
                .setHasTextField(true)
                .build();
        Checkbox fourthCheckbox = new CheckboxBuilder()
                .setText("Fourth Test Checkbox")
                .setHasTextField(true)
                .build();
        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .setMultipleSelect(true)
                .setMinSelect(1)
                .setMaxSelect(3)
                .setCheckboxes(List.of(firstCheckbox, secondCheckbox, thirdCheckbox, fourthCheckbox))
                .build();
        Question question = new QuestionBuilder()
                .setText("Test Question")
                .setHasCheckbox(true)
                .setCheckboxGroup(checkboxGroup)
                .build();
        Survey survey = new SurveyBuilder()
                .setName("Complete Survey")
                .addQuestionGroup("QuestionGroup with Question")
                .addQuestionToGroup(question, 0)
                .build();

        List<String> errorMessages = surveyService.checkIfAnythingEmpty(survey);

        assertTrue(errorMessages.isEmpty());
    }
}