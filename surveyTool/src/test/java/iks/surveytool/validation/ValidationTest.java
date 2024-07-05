package iks.surveytool.validation;

import iks.surveytool.entities.InvalidEntityException;
import iks.surveytool.entities.QuestionGroup;
import iks.surveytool.entities.Survey;
import iks.surveytool.entities.SurveyResponse;
import iks.surveytool.entities.answer.OpenAnswer;
import iks.surveytool.entities.question.ChoiceQuestion;
import iks.surveytool.entities.question.ChoiceQuestionChoice;
import iks.surveytool.entities.question.OpenQuestion;
import iks.surveytool.utils.builder.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testing validation of entities")
class ValidationTest {
    @Test
    @DisplayName("Failed validation - Survey missing QuestionGroups")
    void surveyIsMissingQuestionGroups() {
        Survey survey = SurveyBuilder.createSurveyWithUserAndDefaultDate(1L, "Survey without QuestionGroup", 1L);
        
        assertThrows(InvalidEntityException.class, survey::validate);
    }
    
    @Test
    @DisplayName("Failed validation - QuestionGroup missing Questions")
    void questionGroupIsMissingQuestion() {
        Survey survey = SurveyBuilder.createSurveyWithUserAndDefaultDate(1L, "Survey with empty QuestionGroup", 1L);
        
        QuestionGroup questionGroupWithQuestion = QuestionGroupBuilder.createQuestionGroupIn(survey,
            1L,
            "QuestionGroup with Question"
        );
        
        QuestionBuilder.createQuestionIn(questionGroupWithQuestion, 1L, 0, "Test Question", false);
        
        QuestionGroupBuilder.createQuestionGroupIn(survey, 2L, "QuestionGroup without Question");
        
        assertThrows(InvalidEntityException.class, survey::validate);
    }
    
    @Test
    @DisplayName("Failed validation - No Checkboxes - No multipleSelect")
    void questionNoCheckboxes() {
        Survey survey = SurveyBuilder.createSurveyWithUserAndDefaultDate(1L,
            "Survey with no checkboxes for question",
            1L
        );
        
        QuestionGroup questionGroupWithQuestion = QuestionGroupBuilder.createQuestionGroupIn(survey,
            1L,
            "QuestionGroup with Question"
        );
        
        QuestionBuilder.createQuestionIn(questionGroupWithQuestion, 1L, 0, "Test Question", 0, 2);
        
        assertThrows(InvalidEntityException.class, survey::validate);
    }
    
    @Test
    @DisplayName("Failed validation - Not enough Checkboxes - No multipleSelect")
    void questionNotEnoughCheckboxes() {
        Survey survey = SurveyBuilder.createSurveyWithUserAndDefaultDate(1L,
            "Survey with not enough checkboxes for question",
            1L
        );
        
        QuestionGroup questionGroupWithQuestion = QuestionGroupBuilder.createQuestionGroupIn(survey,
            1L,
            "QuestionGroup with Question"
        );
        
        ChoiceQuestion question = QuestionBuilder.createQuestionIn(questionGroupWithQuestion,
            1L,
            0,
            "Test Question",
            0,
            2
        );
        
        ChoiceBuilder.createChoiceIn(question, 1L, "First Test Checkbox", false);
        
        assertThrows(InvalidEntityException.class, survey::validate);
    }
    
    @Test
    @DisplayName("Failed validation - Not enough Checkboxes - With multipleSelect (max: 4)")
    void questionNotEnoughCheckboxesMultipleSelect() {
        Survey survey = SurveyBuilder.createSurveyWithUserAndDefaultDate(1L,
            "Survey with not enough checkboxes for question",
            1L
        );
        
        QuestionGroup questionGroupWithQuestion = QuestionGroupBuilder.createQuestionGroup(1L,
            "QuestionGroup with Question"
        );
        
        ChoiceQuestion question = QuestionBuilder.createQuestion(1L, 0, "Test Question", 2, 4);
        
        ChoiceQuestionChoice firstCheckbox = ChoiceBuilder.createChoice(1L, "First Test Checkbox", false);
        ChoiceQuestionChoice secondCheckbox = ChoiceBuilder.createChoice(2L, "Second Test Checkbox", true);
        ChoiceQuestionChoice thirdCheckbox = ChoiceBuilder.createChoice(3L, "Third Test Checkbox", true);
        
        question.setChoices(List.of(firstCheckbox, secondCheckbox, thirdCheckbox));
        
        questionGroupWithQuestion.setChoiceQuestions(List.of(question));
        
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));
        
        assertThrows(InvalidEntityException.class, survey::validate);
    }
    
    @Test
    @DisplayName("Failed validation - Survey name missing")
    void surveyBasicInfoMissing() {
        Survey survey = SurveyBuilder.createDefaultSurvey();
        survey.setName(null);
        
        QuestionGroup questionGroupWithQuestion = QuestionGroupBuilder.createQuestionGroupIn(survey,
            1L,
            "QuestionGroup with Question"
        );
        
        QuestionBuilder.createQuestionIn(questionGroupWithQuestion, 1L, 0, "Test Question", false);
        
        assertThrows(InvalidEntityException.class, survey::validate);
    }
    
    @Test
    @DisplayName("Failed validation - endDate before startDate")
    void surveyEndDateBeforeStartDate() {
        Survey survey = SurveyBuilder.createDefaultSurvey();
        
        survey.setEndDate(survey.getStartDate().minusDays(2));
        
        QuestionGroup questionGroupWithQuestion = QuestionGroupBuilder.createQuestionGroupIn(survey,
            1L,
            "QuestionGroup with Question"
        );
        
        QuestionBuilder.createQuestionIn(questionGroupWithQuestion, 1L, 0, "Test Question", false);
        
        assertThrows(InvalidEntityException.class, survey::validate);
    }
    
    @Test
    @DisplayName("Failed validation - QuestionGroup missing title")
    void questionGroupIsMissingTitle() {
        Survey survey = SurveyBuilder.createDefaultSurvey();
        
        QuestionGroup firstQuestionGroupWithQuestion = QuestionGroupBuilder.createQuestionGroupIn(survey, 1L, null);
        QuestionGroup secondQuestionGroupWithQuestion = QuestionGroupBuilder.createQuestionGroupIn(survey,
            2L,
            "Test Title"
        );
        
        QuestionBuilder.createQuestionIn(firstQuestionGroupWithQuestion, 1L, 0, "Test Question", false);
        QuestionBuilder.createQuestionIn(secondQuestionGroupWithQuestion, 2L, 0, "Test Question", false);
        
        assertThrows(InvalidEntityException.class, survey::validate);
    }
    
    @Test
    @DisplayName("Failed validation - Question missing text")
    void questionIsMissingText() {
        Survey survey = SurveyBuilder.createDefaultSurvey();
        
        QuestionGroup questionGroupWithQuestion = QuestionGroupBuilder.createQuestionGroupIn(survey, 1L,
            "QuestionGroup with Questions"
        );
        
        QuestionBuilder.createQuestionIn(questionGroupWithQuestion, 1L, 0, null, false);
        QuestionBuilder.createQuestionIn(questionGroupWithQuestion, 2L, 0, "Test", false);
        
        assertThrows(InvalidEntityException.class, survey::validate);
    }
    
    @Test
    @DisplayName("Failed validation - minSelect > maxSelect")
    void minSelectGreaterThanMaxSelect() {
        ChoiceQuestionChoice firstCheckbox = ChoiceBuilder.createChoice(1L, "First Test Checkbox", false);
        ChoiceQuestionChoice secondCheckbox = ChoiceBuilder.createChoice(2L, "Second Test Checkbox", true);
        ChoiceQuestionChoice thirdCheckbox = ChoiceBuilder.createChoice(3L, "Third Test Checkbox", true);
        
        ChoiceQuestion question = QuestionBuilder.createQuestion(1L, 0, "Test Question", 3, 2);
        question.setChoices(List.of(firstCheckbox, secondCheckbox, thirdCheckbox));
        
        QuestionGroup questionGroupWithQuestion = QuestionGroupBuilder.createQuestionGroup(1L,
            "QuestionGroup with Question"
        );
        questionGroupWithQuestion.setChoiceQuestions(List.of(question));
        
        Survey survey = SurveyBuilder.createSurveyWithUserAndDefaultDate(1L, "Complete Survey", 1L);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));
        
        assertThrows(InvalidEntityException.class, survey::validate);
    }
    
    @Test
    @DisplayName("Failed validation - minSelect < 0")
    void minSelectLessThanZero() {
        ChoiceQuestionChoice firstCheckbox = ChoiceBuilder.createChoice(1L, "First Test Checkbox", false);
        ChoiceQuestionChoice secondCheckbox = ChoiceBuilder.createChoice(2L, "Second Test Checkbox", true);
        ChoiceQuestionChoice thirdCheckbox = ChoiceBuilder.createChoice(3L, "Third Test Checkbox", true);
        
        ChoiceQuestion question = QuestionBuilder.createQuestion(1L, 0, "Test Question", -1, 2);
        question.setChoices(List.of(firstCheckbox, secondCheckbox, thirdCheckbox));
        
        QuestionGroup questionGroupWithQuestion = QuestionGroupBuilder.createQuestionGroup(1L,
            "QuestionGroup with Question"
        );
        questionGroupWithQuestion.setChoiceQuestions(List.of(question));
        
        Survey survey = SurveyBuilder.createSurveyWithUserAndDefaultDate(1L, "Complete Survey", 1L);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));
        
        assertThrows(InvalidEntityException.class, survey::validate);
    }
    
    @Test
    @DisplayName("Successful validation - maxSelect = 1")
    void maxSelectLessThanTwo() throws InvalidEntityException {
        ChoiceQuestionChoice firstCheckbox = ChoiceBuilder.createChoice(1L, "First Test Checkbox", false);
        ChoiceQuestionChoice secondCheckbox = ChoiceBuilder.createChoice(2L, "Second Test Checkbox", true);
        ChoiceQuestionChoice thirdCheckbox = ChoiceBuilder.createChoice(3L, "Third Test Checkbox", true);
        
        ChoiceQuestion question = QuestionBuilder.createQuestion(1L, 0, "Test Question", 0, 1);
        question.setChoices(List.of(firstCheckbox, secondCheckbox, thirdCheckbox));
        
        QuestionGroup questionGroupWithQuestion = QuestionGroupBuilder.createQuestionGroup(1L,
            "QuestionGroup with Question"
        );
        questionGroupWithQuestion.setChoiceQuestions(List.of(question));
        
        Survey survey = SurveyBuilder.createSurveyWithUserAndDefaultDate(1L, "Complete Survey", 1L);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));
        
        survey.validate();
    }
    
    @Test
    @DisplayName("Failed validation - Checkbox missing text")
    void checkboxIsMissingText() {
        Survey survey = SurveyBuilder.createSurveyWithUserAndDefaultDate(1L, "Complete Survey", 1L);
        
        QuestionGroup questionGroupWithQuestion = QuestionGroupBuilder.createQuestionGroupIn(survey,
            1L,
            "QuestionGroup with Question"
        );
        
        ChoiceQuestion question = QuestionBuilder.createQuestionIn(questionGroupWithQuestion,
            1L,
            0,
            "Test Question",
            0,
            2
        );
        
        ChoiceBuilder.createChoiceIn(question, 1L, "First Test Checkbox", false);
        ChoiceBuilder.createChoiceIn(question, 2L, null, true);
        ChoiceBuilder.createChoiceIn(question, 3L, "Third Test Checkbox", true);
        
        assertThrows(InvalidEntityException.class, survey::validate);
    }
    
    @Test
    @DisplayName("Successful validation - User missing")
    void surveyMissingUser() {
        Survey survey = SurveyBuilder.createSurveyWithDefaultDate(1L, "User missing");
        
        QuestionGroup questionGroupWithQuestion = QuestionGroupBuilder.createQuestionGroupIn(survey,
            1L,
            "QuestionGroup with Question"
        );
        
        ChoiceQuestion question = QuestionBuilder.createQuestionIn(questionGroupWithQuestion,
            1L,
            0,
            "Test Question",
            1,
            3
        );
        
        ChoiceBuilder.createChoiceIn(question, 1L, "First Test Checkbox", false);
        ChoiceBuilder.createChoiceIn(question, 2L, "Second Test Checkbox", true);
        ChoiceBuilder.createChoiceIn(question, 3L, "Third Test Checkbox", true);
        ChoiceBuilder.createChoiceIn(question, 4L, "Fourth Test Checkbox", false);
        
        assertDoesNotThrow(survey::validate);
    }
    
    @Test
    @DisplayName("Successful validation - Complete survey")
    void surveyIsComplete() {
        Survey survey = SurveyBuilder.createSurveyWithUserAndDefaultDate(1L, "Complete Survey", 1L);
        
        QuestionGroup questionGroupWithQuestion = QuestionGroupBuilder.createQuestionGroupIn(survey,
            1L,
            "QuestionGroup with Question"
        );
        
        ChoiceQuestion question = QuestionBuilder.createQuestionIn(questionGroupWithQuestion,
            1L,
            0,
            "Test Question",
            1,
            3
        );
        
        ChoiceBuilder.createChoiceIn(question, 1L, "First Test Checkbox", false);
        ChoiceBuilder.createChoiceIn(question, 2L, "Second Test Checkbox", true);
        ChoiceBuilder.createChoiceIn(question, 3L, "Third Test Checkbox", true);
        ChoiceBuilder.createChoiceIn(question, 4L, "Fourth Test Checkbox", false);
        
        assertDoesNotThrow(survey::validate);
    }
    
    @Test
    @DisplayName("Failed validation - Answer missing response")
    void answerMissingUser() {
        Survey survey = SurveyBuilder.createDefaultSurvey();
        
        QuestionGroup group = QuestionGroupBuilder.createQuestionGroupIn(survey, 1L, "Bla");
        
        OpenQuestion question = QuestionBuilder.createQuestionIn(group, 1L, 0, "Test Question", false);
        
        OpenAnswer answer = AnswerBuilder.createAnswer(1L, null, question);
        
        assertThrows(InvalidEntityException.class, answer::validate);
    }
    
    @Test
    @DisplayName("Failed validation - Answer missing Text when Checkbox has text field")
    void answerMissingTextWhenCheckboxHasTextField() {
        Survey survey = SurveyBuilder.createDefaultSurvey();
        
        QuestionGroup group = QuestionGroupBuilder.createQuestionGroupIn(survey, 1L, "Bla");
        
        ChoiceQuestionChoice checkbox = ChoiceBuilder.createChoice(1L, "Test Checkbox", true);
        
        ChoiceQuestion question = new ChoiceQuestion(0, "Test Question".getBytes(StandardCharsets.UTF_8), 0, 1);
        question.setChoices(List.of(checkbox));
        
        group.setChoiceQuestions(List.of(question));
        
        SurveyResponse response = new SurveyResponseBuilder().createResponse(1L, survey, null);
        response.setChoiceAnswers(List.of(checkbox));
        
        assertThrows(InvalidEntityException.class, response::validate);
    }
    
    @Test
    @DisplayName("Failed validation - Answer missing Text when text Question")
    void answerMissingTextWhenQuestionWithTextField() {
        Survey survey = SurveyBuilder.createDefaultSurvey();
        
        QuestionGroup group = QuestionGroupBuilder.createQuestionGroupIn(survey, 1L, "Bla");
        
        QuestionBuilder.createQuestionIn(group, 1L, 0, "Test Question", true);
        
        SurveyResponse response = new SurveyResponseBuilder().createResponse(1L, survey, null);
        
        assertThrows(InvalidEntityException.class, response::validate);
    }
    
    @Test
    @DisplayName("Successful validation - Answer missing Text when text Question and not required")
    void answerMissingTextWhenQuestionWithTextFieldNotRequired() throws InvalidEntityException {
        Survey survey = SurveyBuilder.createDefaultSurvey();
        
        QuestionGroup group = QuestionGroupBuilder.createQuestionGroupIn(survey, 1L, "Bla");
        
        QuestionBuilder.createQuestionIn(group, 1L, 0, "Test Question", false);
        
        SurveyResponse response = new SurveyResponseBuilder().createResponse(1L, survey, 1L);
        
        response.validate();
    }
    
    @Test
    @DisplayName("Successful validation - Complete Answer to text Question")
    void answerToTextQuestionIsComplete() throws InvalidEntityException {
        Survey survey = SurveyBuilder.createDefaultSurvey();
        
        QuestionGroup group = QuestionGroupBuilder.createQuestionGroupIn(survey, 1L, "Bla");
        
        OpenQuestion question = QuestionBuilder.createQuestionIn(group, 1L, 0, "Test Question", false);
        
        SurveyResponse response = new SurveyResponseBuilder().createResponse(1L, survey, 1L);
        
        AnswerBuilder.createAnswerIn(response, 1L, "Test", question);
        
        response.validate();
    }
    
    @Test
    @DisplayName("Successful validation - Complete Answer to multiple choice Question")
    void answerToChoiceQuestionIsComplete() throws InvalidEntityException {
        Survey survey = SurveyBuilder.createDefaultSurvey();
        
        QuestionGroup group = QuestionGroupBuilder.createQuestionGroupIn(survey, 1L, "Bla");
        
        ChoiceQuestion question = QuestionBuilder.createQuestionIn(group, 1L, 0, "Test Question", 1, 1);
        
        ChoiceQuestionChoice checkbox = ChoiceBuilder.createChoiceIn(question, 1L, "Test Checkbox", false);
        
        SurveyResponse response = new SurveyResponseBuilder().createResponse(1L, survey, 1L);
        
        response.setChoiceAnswers(List.of(checkbox));
        
        response.validate();
    }
}