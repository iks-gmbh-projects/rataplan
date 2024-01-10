package iks.surveytool.validation;

import iks.surveytool.entities.*;
import iks.surveytool.utils.builder.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testing validation of entities")
class ValidationTest {

    @Test
    @DisplayName("Failed validation - Survey missing QuestionGroups")
    void surveyIsMissingQuestionGroups() {
        Survey survey = new SurveyBuilder()
                .createSurveyWithUserAndDefaultDate(1L, "Survey without QuestionGroup", 1L);

        assertThrows(InvalidEntityException.class, survey::validate);
    }

    @Test
    @DisplayName("Failed validation - QuestionGroup missing Questions")
    void questionGroupIsMissingQuestion() {
        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, false);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Question");
        questionGroupWithQuestion.setQuestions(List.of(question));
        QuestionGroup questionGroupWithoutQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(2L, "QuestionGroup without Question");

        Survey survey = new SurveyBuilder()
                .createSurveyWithUserAndDefaultDate(1L, "Survey with empty QuestionGroup", 1L);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion, questionGroupWithoutQuestion));
        
        assertThrows(InvalidEntityException.class, survey::validate);
    }

    @Test
    @DisplayName("Failed validation - No Checkboxes - No multipleSelect")
    void questionNoCheckboxes() {
        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .createCheckboxGroup(1L, false, 0, 2);

        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, true);
        question.setCheckboxGroup(checkboxGroup);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Question");
        questionGroupWithQuestion.setQuestions(List.of(question));

        Survey survey = new SurveyBuilder()
                .createSurveyWithUserAndDefaultDate(1L, "Survey with no checkboxes for question", 1L);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));
        
        assertThrows(InvalidEntityException.class, survey::validate);
    }

    @Test
    @DisplayName("Failed validation - Not enough Checkboxes - No multipleSelect")
    void questionNotEnoughCheckboxes() {
        Checkbox onlyCheckbox = new CheckboxBuilder()
                .createCheckbox(1L, "First Test Checkbox", false);

        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .createCheckboxGroup(1L, false, 0, 2);
        checkboxGroup.setCheckboxes(List.of(onlyCheckbox));

        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, true);
        question.setCheckboxGroup(checkboxGroup);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Question");
        questionGroupWithQuestion.setQuestions(List.of(question));

        Survey survey = new SurveyBuilder()
                .createSurveyWithUserAndDefaultDate(1L, "Survey with not enough checkboxes for question", 1L);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));
        
        assertThrows(InvalidEntityException.class, survey::validate);
    }

    @Test
    @DisplayName("Failed validation - Not enough Checkboxes - With multipleSelect (max: 4)")
    void questionNotEnoughCheckboxesMultipleSelect() {
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
                .createSurveyWithUserAndDefaultDate(1L, "Survey with not enough checkboxes for question", 1L);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));
        
        assertThrows(InvalidEntityException.class, survey::validate);
    }

    @Test
    @DisplayName("Failed validation - Question with hasCheckbox == true, but no CheckboxGroup")
    void questionHasCheckboxTrueButNoCheckboxGroup() {
        Question firstQuestion = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, true);
        Question secondQuestion = new QuestionBuilder()
                .createQuestion(2L, "Test Question", false, false);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Question");
        questionGroupWithQuestion.setQuestions(List.of(firstQuestion, secondQuestion));

        Survey survey = new SurveyBuilder()
                .createSurveyWithUserAndDefaultDate(1L, "Survey with not enough checkboxes for question", 1L);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));
        
        assertThrows(InvalidEntityException.class, survey::validate);
    }

    @Test
    @DisplayName("Failed validation - Survey name missing")
    void surveyBasicInfoMissing() {
        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, false);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Question");
        questionGroupWithQuestion.setQuestions(List.of(question));

        Survey survey = new SurveyBuilder()
                .createDefaultSurvey();
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        survey.setName(null);

        assertThrows(InvalidEntityException.class, survey::validate);
    }

    @Test
    @DisplayName("Failed validation - endDate before startDate")
    void surveyEndDateBeforeStartDate() {
        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, false);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Question");
        questionGroupWithQuestion.setQuestions(List.of(question));

        Survey survey = new SurveyBuilder()
                .createDefaultSurvey();
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        survey.setEndDate(survey.getStartDate().minusDays(2));

        assertThrows(InvalidEntityException.class, survey::validate);
    }

    @Test
    @DisplayName("Failed validation - startDate in past")
    void startDateInPast() {
        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, false);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Question");
        questionGroupWithQuestion.setQuestions(List.of(question));

        Survey survey = new SurveyBuilder()
                .createDefaultSurvey();
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        survey.setStartDate(ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneId.systemDefault()));

        assertThrows(InvalidEntityException.class, survey::validate);
    }

    @Test
    @DisplayName("Failed validation - QuestionGroup missing title")
    void questionGroupIsMissingTitle() {
        Question firstQuestion = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, false);
        Question secondQuestion = new QuestionBuilder()
                .createQuestion(2L, "Test Question", false, false);

        QuestionGroup firstQuestionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, null);
        firstQuestionGroupWithQuestion.setQuestions(List.of(firstQuestion));
        QuestionGroup secondQuestionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(2L, "Test Title");
        secondQuestionGroupWithQuestion.setQuestions(List.of(secondQuestion));

        Survey survey = new SurveyBuilder()
                .createDefaultSurvey();
        survey.setQuestionGroups(List.of(firstQuestionGroupWithQuestion, secondQuestionGroupWithQuestion));

        assertThrows(InvalidEntityException.class, survey::validate);
    }

    @Test
    @DisplayName("Failed validation - Question missing text")
    void questionIsMissingText() {
        Question firstQuestion = new QuestionBuilder()
                .createQuestion(1L, null, false, false);
        Question secondQuestion = new QuestionBuilder()
                .createQuestion(2L, "Test", false, false);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Questions");
        questionGroupWithQuestion.setQuestions(List.of(firstQuestion, secondQuestion));

        Survey survey = new SurveyBuilder()
                .createDefaultSurvey();
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        assertThrows(InvalidEntityException.class, survey::validate);
    }

    @Test
    @DisplayName("Failed validation - minSelect > maxSelect")
    void minSelectGreaterThanMaxSelect() {
        Checkbox firstCheckbox = new CheckboxBuilder()
                .createCheckbox(1L, "First Test Checkbox", false);
        Checkbox secondCheckbox = new CheckboxBuilder()
                .createCheckbox(2L, "Second Test Checkbox", true);
        Checkbox thirdCheckbox = new CheckboxBuilder()
                .createCheckbox(3L, "Third Test Checkbox", true);

        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .createCheckboxGroup(1L, true, 3, 2);
        checkboxGroup.setCheckboxes(List.of(firstCheckbox, secondCheckbox, thirdCheckbox));

        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, true);
        question.setCheckboxGroup(checkboxGroup);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Question");
        questionGroupWithQuestion.setQuestions(List.of(question));

        Survey survey = new SurveyBuilder()
                .createSurveyWithUserAndDefaultDate(1L, "Complete Survey", 1L);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        assertThrows(InvalidEntityException.class, survey::validate);
    }

    @Test
    @DisplayName("Failed validation - minSelect < 0")
    void minSelectLessThanZero() {
        Checkbox firstCheckbox = new CheckboxBuilder()
                .createCheckbox(1L, "First Test Checkbox", false);
        Checkbox secondCheckbox = new CheckboxBuilder()
                .createCheckbox(2L, "Second Test Checkbox", true);
        Checkbox thirdCheckbox = new CheckboxBuilder()
                .createCheckbox(3L, "Third Test Checkbox", true);

        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .createCheckboxGroup(1L, true, -1, 2);
        checkboxGroup.setCheckboxes(List.of(firstCheckbox, secondCheckbox, thirdCheckbox));

        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, true);
        question.setCheckboxGroup(checkboxGroup);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Question");
        questionGroupWithQuestion.setQuestions(List.of(question));

        Survey survey = new SurveyBuilder()
                .createSurveyWithUserAndDefaultDate(1L, "Complete Survey", 1L);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        assertThrows(InvalidEntityException.class, survey::validate);
    }

    @Test
    @DisplayName("Failed validation - maxSelect < 2")
    void maxSelectLessThanTwo() {
        Checkbox firstCheckbox = new CheckboxBuilder()
                .createCheckbox(1L, "First Test Checkbox", false);
        Checkbox secondCheckbox = new CheckboxBuilder()
                .createCheckbox(2L, "Second Test Checkbox", true);
        Checkbox thirdCheckbox = new CheckboxBuilder()
                .createCheckbox(3L, "Third Test Checkbox", true);

        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .createCheckboxGroup(1L, true, 0, 1);
        checkboxGroup.setCheckboxes(List.of(firstCheckbox, secondCheckbox, thirdCheckbox));

        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, true);
        question.setCheckboxGroup(checkboxGroup);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Question");
        questionGroupWithQuestion.setQuestions(List.of(question));

        Survey survey = new SurveyBuilder()
                .createSurveyWithUserAndDefaultDate(1L, "Complete Survey", 1L);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        assertThrows(InvalidEntityException.class, survey::validate);
    }

    @Test
    @DisplayName("Failed validation - Checkbox missing text")
    void checkboxIsMissingText() {
        Checkbox firstCheckbox = new CheckboxBuilder()
                .createCheckbox(1L, "First Test Checkbox", false);
        Checkbox secondCheckbox = new CheckboxBuilder()
                .createCheckbox(2L, null, true);
        Checkbox thirdCheckbox = new CheckboxBuilder()
                .createCheckbox(3L, "Third Test Checkbox", true);

        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .createCheckboxGroup(1L, true, 0, 2);
        checkboxGroup.setCheckboxes(List.of(firstCheckbox, secondCheckbox, thirdCheckbox));

        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, true);
        question.setCheckboxGroup(checkboxGroup);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Question");
        questionGroupWithQuestion.setQuestions(List.of(question));

        Survey survey = new SurveyBuilder()
                .createSurveyWithUserAndDefaultDate(1L, "Complete Survey", 1L);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        assertThrows(InvalidEntityException.class, survey::validate);
    }

    @Test
    @DisplayName("Failed validation - Question required but minSelect = 0")
    void requiredButMinSelectZero() {
        Checkbox firstCheckbox = new CheckboxBuilder()
                .createCheckbox(1L, "First Test Checkbox", false);
        Checkbox secondCheckbox = new CheckboxBuilder()
                .createCheckbox(2L, "Second Test Checkbox", true);
        Checkbox thirdCheckbox = new CheckboxBuilder()
                .createCheckbox(3L, "Third Test Checkbox", true);

        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .createCheckboxGroup(1L, true, 0, 2);
        checkboxGroup.setCheckboxes(List.of(firstCheckbox, secondCheckbox, thirdCheckbox));

        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", true, true);
        question.setCheckboxGroup(checkboxGroup);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Question");
        questionGroupWithQuestion.setQuestions(List.of(question));

        Survey survey = new SurveyBuilder()
                .createSurveyWithUserAndDefaultDate(1L, "Complete Survey", 1L);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        assertThrows(InvalidEntityException.class, survey::validate);
    }

    @Test
    @DisplayName("Successful validation - User missing")
    void surveyMissingUser() {
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
                .createSurveyWithDefaultDate(1L, "User missing");
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        assertDoesNotThrow(survey::validate);
    }

    @Test
    @DisplayName("Successful validation - Complete survey")
    void surveyIsComplete() {
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
                .createSurveyWithUserAndDefaultDate(1L, "Complete Survey", 1L);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        assertDoesNotThrow(survey::validate);
    }

    @Test
    @DisplayName("Failed validation - Answer missing response")
    void answerMissingUser() {
        Survey survey = new SurveyBuilder()
                .createDefaultSurvey();

        QuestionGroup group = new QuestionGroupBuilder()
                .createQuestionGroupIn(survey, 1L, "Bla");

        Question question = new QuestionBuilder()
                .createQuestionIn(group, 1L, "Test Question", false, false);

        Answer answer = new AnswerBuilder()
                .createAnswer(1L, "Test Answer", question, null);

        assertFalse(answer.validate());
    }

    @Test
    @DisplayName("Failed validation - Answer missing Checkbox")
    void answerMissingQuestion() {
        Survey survey = new SurveyBuilder()
                .createDefaultSurvey();

        QuestionGroup group = new QuestionGroupBuilder()
                .createQuestionGroupIn(survey, 1L, "Bla");

        Question question = new QuestionBuilder()
                .createQuestionIn(group, 1L, "Test Question", false, true);

        SurveyResponse response = new SurveyResponseBuilder()
                .createResponse(1L, survey, null);

        Answer answer = new AnswerBuilder()
                .createAnswerIn(response, 1L, "Test Answer", question, null);

        assertFalse(answer.validate());
    }

    @Test
    @DisplayName("Failed validation - Answer missing Text when Checkbox has text field")
    void answerMissingTextWhenCheckboxHasTextField() {
        Survey survey = new SurveyBuilder()
                .createDefaultSurvey();

        QuestionGroup group = new QuestionGroupBuilder()
                .createQuestionGroupIn(survey, 1L, "Bla");

        Question question = new QuestionBuilder()
                .createQuestionIn(group, 1L, "Test Question", true, true);
        
        Checkbox checkbox = new CheckboxBuilder()
                .createCheckbox(1L, "Test Checkbox", true);
        
        CheckboxGroup checkboxGroup = new CheckboxGroup(false, 0, 1, List.of(checkbox));
        question.setCheckboxGroup(checkboxGroup);

        SurveyResponse response = new SurveyResponseBuilder()
                .createResponse(1L, survey, null);

        Answer answer = new AnswerBuilder()
                .createAnswerIn(response, 1L, null, question, List.of(checkbox));

        assertFalse(answer.validate());
    }

    @Test
    @DisplayName("Failed validation - Answer missing Text when text Question")
    void answerMissingTextWhenQuestionWithTextField() {
        Survey survey = new SurveyBuilder()
                .createDefaultSurvey();

        QuestionGroup group = new QuestionGroupBuilder()
                .createQuestionGroupIn(survey, 1L, "Bla");

        Question question = new QuestionBuilder()
                .createQuestionIn(group, 1L, "Test Question", true, false);

        SurveyResponse response = new SurveyResponseBuilder()
                .createResponse(1L, survey, null);

        Answer answer = new AnswerBuilder()
                .createAnswerIn(response, 1L, null, question, null);

        assertFalse(answer.validate());
    }
    
    @Test
    @DisplayName("Successful validation - Answer missing Text when text Question and not required")
    void answerMissingTextWhenQuestionWithTextFieldNotRequired() {
        Survey survey = new SurveyBuilder()
            .createDefaultSurvey();
        
        QuestionGroup group = new QuestionGroupBuilder()
            .createQuestionGroupIn(survey, 1L, "Bla");
        
        Question question = new QuestionBuilder()
            .createQuestionIn(group, 1L, "Test Question", false, false);
        
        SurveyResponse response = new SurveyResponseBuilder()
            .createResponse(1L, survey, null);
        
        Answer answer = new AnswerBuilder()
            .createAnswerIn(response, 1L, null, question, null);
        
        assertTrue(answer.validate());
    }

    @Test
    @DisplayName("Successful validation - Complete Answer to text Question")
    void answerToTextQuestionIsComplete() {
        Survey survey = new SurveyBuilder()
                .createDefaultSurvey();

        QuestionGroup group = new QuestionGroupBuilder()
                .createQuestionGroupIn(survey, 1L, "Bla");

        Question question = new QuestionBuilder()
                .createQuestionIn(group, 1L, "Test Question", false, false);

        SurveyResponse response = new SurveyResponseBuilder()
                .createResponse(1L, survey, null);

        Answer answer = new AnswerBuilder()
                .createAnswerIn(response, 1L, "Test", question, null);

        assertTrue(answer.validate());
    }

    @Test
    @DisplayName("Successful validation - Complete Answer to multiple choice Question")
    void answerToChoiceQuestionIsComplete() {
        Survey survey = new SurveyBuilder()
                .createDefaultSurvey();

        QuestionGroup group = new QuestionGroupBuilder()
                .createQuestionGroupIn(survey, 1L, "Bla");

        Question question = new QuestionBuilder()
                .createQuestionIn(group, 1L, "Test Question", false, true);

        Checkbox checkbox = new CheckboxBuilder()
                .createCheckbox(1L, "Test Checkbox", false);
        
        CheckboxGroup checkboxGroup = new CheckboxGroup(true, 1, 1, List.of(checkbox));
        question.setCheckboxGroup(checkboxGroup);

        SurveyResponse response = new SurveyResponseBuilder()
                .createResponse(1L, survey, null);

        Answer answer = new AnswerBuilder()
                .createAnswerIn(response, 1L, null, question, List.of(checkbox));

        assertTrue(answer.validate());
    }

}
