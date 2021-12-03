package iks.surveytool.validation;

import iks.surveytool.builder.*;
import iks.surveytool.entities.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testing validation of entities")
class ValidationTest {

    @Test
    @DisplayName("Failed validation - Survey missing QuestionGroups")
    void surveyIsMissingQuestionGroups() {
        User user = new UserBuilder().createUser(1L, "Test Person");

        Survey survey = new SurveyBuilder()
                .createSurveyWithUserAndDefaultDate(1L, "Survey without QuestionGroup", user);

        assertFalse(survey.validate());
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

        User user = new UserBuilder().createUser(1L, "Test Person");

        Survey survey = new SurveyBuilder()
                .createSurveyWithUserAndDefaultDate(1L, "Survey with empty QuestionGroup", user);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion, questionGroupWithoutQuestion));

        assertFalse(survey.validate());
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

        User user = new UserBuilder().createUser(1L, "Test Person");

        Survey survey = new SurveyBuilder()
                .createSurveyWithUserAndDefaultDate(1L, "Survey with no checkboxes for question", user);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        assertFalse(survey.validate());
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

        User user = new UserBuilder().createUser(1L, "Test Person");

        Survey survey = new SurveyBuilder()
                .createSurveyWithUserAndDefaultDate(1L, "Survey with not enough checkboxes for question", user);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        assertFalse(survey.validate());
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

        User user = new UserBuilder().createUser(1L, "Test Person");

        Survey survey = new SurveyBuilder()
                .createSurveyWithUserAndDefaultDate(1L, "Survey with not enough checkboxes for question", user);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        assertFalse(survey.validate());
    }

    @Test
    @DisplayName("Failed validation - Question with hasCheckbox == true, but no CheckboxGroup")
    void questionHasCheckboxTrueButNoCheckboxGroup() {
        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, true);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Question");
        questionGroupWithQuestion.setQuestions(List.of(question));

        User user = new UserBuilder().createUser(1L, "Test Person");

        Survey survey = new SurveyBuilder()
                .createSurveyWithUserAndDefaultDate(1L, "Survey with not enough checkboxes for question", user);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        assertFalse(survey.validate());
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

        assertFalse(survey.validate());
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

        assertFalse(survey.validate());
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

        survey.setStartDate(LocalDateTime.of(2000, 1, 1, 12, 0));

        assertFalse(survey.validate());
    }

    @Test
    @DisplayName("Failed validation - QuestionGroup missing title")
    void questionGroupIsMissingTitle() {
        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, false);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, null);
        questionGroupWithQuestion.setQuestions(List.of(question));

        Survey survey = new SurveyBuilder()
                .createDefaultSurvey();
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        assertFalse(survey.validate());
    }

    @Test
    @DisplayName("Failed validation - Question missing text")
    void questionIsMissingText() {
        Question question = new QuestionBuilder()
                .createQuestion(1L, null, false, false);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Question");
        questionGroupWithQuestion.setQuestions(List.of(question));

        Survey survey = new SurveyBuilder()
                .createDefaultSurvey();
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        assertFalse(survey.validate());
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

        User user = new UserBuilder().createUser(1L, "Test Person");

        Survey survey = new SurveyBuilder()
                .createSurveyWithUserAndDefaultDate(1L, "Complete Survey", user);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        assertFalse(survey.validate());
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

        User user = new UserBuilder().createUser(1L, "Test Person");

        Survey survey = new SurveyBuilder()
                .createSurveyWithUserAndDefaultDate(1L, "Complete Survey", user);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        assertFalse(survey.validate());
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

        User user = new UserBuilder().createUser(1L, "Test Person");

        Survey survey = new SurveyBuilder()
                .createSurveyWithUserAndDefaultDate(1L, "Complete Survey", user);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        assertFalse(survey.validate());
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

        User user = new UserBuilder().createUser(1L, "Test Person");

        Survey survey = new SurveyBuilder()
                .createSurveyWithUserAndDefaultDate(1L, "Complete Survey", user);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        assertFalse(survey.validate());
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

        User user = new UserBuilder().createUser(1L, "Test Person");

        Survey survey = new SurveyBuilder()
                .createSurveyWithUserAndDefaultDate(1L, "Complete Survey", user);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        assertFalse(survey.validate());
    }

    @Test
    @DisplayName("Failed validation - User missing")
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

        assertFalse(survey.validate());
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

        User user = new UserBuilder().createUser(1L, "Test Person");

        Survey survey = new SurveyBuilder()
                .createSurveyWithUserAndDefaultDate(1L, "Complete Survey", user);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        assertTrue(survey.validate());
    }

    @Test
    @DisplayName("Failed validation - User missing name")
    void userMissingName() {
        User user = new UserBuilder()
                .createUser(1L, null);

        assertFalse(user.validate());
    }

    @Test
    @DisplayName("Successful validation - User")
    void userIsComplete() {
        User user = new UserBuilder()
                .createUser(1L, "Test User");

        assertTrue(user.validate());
    }

    @Test
    @DisplayName("Failed validation - Answer missing user")
    void answerMissingUser() {
        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, false);

        Answer answer = new AnswerBuilder()
                .createAnswer(1L, "Test Answer", null, question, null);

        assertFalse(answer.validate());
    }

    @Test
    @DisplayName("Failed validation - Answer missing Checkbox")
    void answerMissingQuestion() {
        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, true);

        User user = new UserBuilder()
                .createUser(1L, "Test User");

        Answer answer = new AnswerBuilder()
                .createAnswer(1L, "Test Answer", user, question, null);

        assertFalse(answer.validate());
    }

    @Test
    @DisplayName("Failed validation - Answer missing Text when Checkbox has text field")
    void answerMissingTextWhenCheckboxHasTextField() {
        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, true);

        User user = new UserBuilder()
                .createUser(1L, "Test User");

        Checkbox checkbox = new CheckboxBuilder()
                .createCheckbox(1L, "Test Checkbox", true);

        Answer answer = new AnswerBuilder()
                .createAnswer(1L, null, user, question, checkbox);

        assertFalse(answer.validate());
    }

    @Test
    @DisplayName("Failed validation - Answer missing Text when text Question")
    void answerMissingTextWhenQuestionWithTextField() {
        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, false);

        User user = new UserBuilder()
                .createUser(1L, "Test User");

        Answer answer = new AnswerBuilder()
                .createAnswer(1L, null, user, question, null);

        assertFalse(answer.validate());
    }

    @Test
    @DisplayName("Successful validation - Complete Answer to text Question")
    void answerToTextQuestionIsComplete() {
        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, false);

        User user = new UserBuilder()
                .createUser(1L, "Test User");

        Answer answer = new AnswerBuilder()
                .createAnswer(1L, "Test", user, question, null);

        assertTrue(answer.validate());
    }

    @Test
    @DisplayName("Successful validation - Complete Answer to multiple choice Question")
    void answerToChoiceQuestionIsComplete() {
        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, true);

        User user = new UserBuilder()
                .createUser(1L, "Test User");

        Checkbox checkbox = new CheckboxBuilder()
                .createCheckbox(1L, "Test Checkbox", false);

        Answer answer = new AnswerBuilder()
                .createAnswer(1L, null, user, question, checkbox);

        assertTrue(answer.validate());
    }

}