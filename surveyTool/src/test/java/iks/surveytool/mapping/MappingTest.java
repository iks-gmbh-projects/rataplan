package iks.surveytool.mapping;

import iks.surveytool.dtos.*;
import iks.surveytool.entities.*;
import iks.surveytool.utils.assertions.MappingAssertions;
import iks.surveytool.utils.builder.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testing Mapper")
public class MappingTest {

    private final ModelMapper modelMapper = new ModelMapper();

    @Test
    @DisplayName("Survey to CompleteSurveyDTO")
    void mapSurveyToDTO() {
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

        CompleteSurveyDTO surveyDTO = modelMapper.map(survey, CompleteSurveyDTO.class);

        MappingAssertions.assertCompleteSurveyDTO(surveyDTO, survey);
    }

    @Test
    @DisplayName("CompleteSurveyDTO to Survey")
    void mapSurveyDTOtoEntity() {
        CheckboxDTO firstCheckboxDTO = new CheckboxDTO();
        firstCheckboxDTO.setId(1L);
        firstCheckboxDTO.setText("First Checkbox DTO");
        firstCheckboxDTO.setHasTextField(false);
        CheckboxDTO secondCheckboxDTO = new CheckboxDTO();
        secondCheckboxDTO.setId(2L);
        secondCheckboxDTO.setText("Second Checkbox DTO");
        secondCheckboxDTO.setHasTextField(true);

        CheckboxGroupDTO checkboxGroupDTO = new CheckboxGroupDTO();
        checkboxGroupDTO.setId(1L);
        checkboxGroupDTO.setMultipleSelect(false);
        checkboxGroupDTO.setCheckboxes(List.of(firstCheckboxDTO, secondCheckboxDTO));

        QuestionDTO firstQuestionDTO = new QuestionDTO();
        firstQuestionDTO.setId(1L);
        firstQuestionDTO.setText("Test Checkbox Question");
        firstQuestionDTO.setRequired(false);
        firstQuestionDTO.setHasCheckbox(true);
        firstQuestionDTO.setCheckboxGroup(checkboxGroupDTO);

        QuestionDTO secondQuestionDTO = new QuestionDTO();
        secondQuestionDTO.setId(1L);
        secondQuestionDTO.setText("Test Text Question");
        secondQuestionDTO.setRequired(true);
        secondQuestionDTO.setHasCheckbox(false);

        QuestionGroupDTO questionGroupDTO = new QuestionGroupDTO();
        questionGroupDTO.setId(1L);
        questionGroupDTO.setTitle("Test QuestionGroup");
        questionGroupDTO.setQuestions(List.of(firstQuestionDTO, secondQuestionDTO));

        CompleteSurveyDTO surveyDTO = new CompleteSurveyDTO();
        surveyDTO.setId(1L);
        surveyDTO.setName("Test Survey");
        surveyDTO.setStartDate(LocalDateTime.of(2050, 1, 1, 12, 0));
        surveyDTO.setEndDate(surveyDTO.getStartDate().plusWeeks(1L));
        surveyDTO.setOpenAccess(false);
        surveyDTO.setAnonymousParticipation(true);
        surveyDTO.setAccessId("Dummy String");
        surveyDTO.setParticipationId("Dummy String");
        surveyDTO.setUserId(1L);
        surveyDTO.setUserName("Max Mustermann");
        surveyDTO.setQuestionGroups(List.of(questionGroupDTO));

        Survey surveyConverted = modelMapper.map(surveyDTO, Survey.class);
        System.out.println(surveyConverted.getUser().getId());
        System.out.println(surveyConverted.getUser().getName());

        MappingAssertions.assertSurvey(surveyConverted, surveyDTO);
    }

    @Test
    @DisplayName("AnswerList to AnswerDTOList")
    void mapAnswerListToAnswerDTOs() {
        Checkbox firstCheckbox = new CheckboxBuilder()
                .createCheckbox(1L, "First Test Checkbox", false);
        Checkbox secondCheckbox = new CheckboxBuilder()
                .createCheckbox(2L, "Second Test Checkbox", true);

        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .createCheckboxGroup(1L, false, 0, 2);
        checkboxGroup.setCheckboxes(List.of(firstCheckbox, secondCheckbox));

        Question firstQuestion = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, false);
        Question secondQuestion = new QuestionBuilder()
                .createQuestion(2L, "Test Question", false, true);
        secondQuestion.setCheckboxGroup(checkboxGroup);

        User user = new User();
        user.setId(1L);
        user.setName("Test User");

        Answer firstAnswer = new AnswerBuilder()
                .createAnswer(1L, "Test Answer", user, firstQuestion, null);
        Answer secondAnswer = new AnswerBuilder()
                .createAnswer(2L, null, user, secondQuestion, firstCheckbox);

        List<Answer> answers = List.of(firstAnswer, secondAnswer);

        Type answerDTOListType = new TypeToken<List<AnswerDTO>>() {
        }.getType();
        List<AnswerDTO> answerDTOList = modelMapper.map(answers, answerDTOListType);

        MappingAssertions.assertAnswerDTOs(answerDTOList, answers);
    }

    @Test
    @DisplayName("AnswerDTOList to AnswerList")
    void mapAnswerDTOsToAnswers() {
        AnswerDTO firstAnswerDTO = new AnswerDTO();
        firstAnswerDTO.setText("Text");
        firstAnswerDTO.setUserId(1L);
        firstAnswerDTO.setUserName("Test User");
        firstAnswerDTO.setQuestionId(1L);
        AnswerDTO secondAnswerDTO = new AnswerDTO();
        secondAnswerDTO.setUserId(1L);
        secondAnswerDTO.setUserName("Test User");
        secondAnswerDTO.setQuestionId(2L);
        secondAnswerDTO.setCheckboxId(1L);

        List<AnswerDTO> answerDTOs = List.of(secondAnswerDTO, secondAnswerDTO);

        Type answerListType = new TypeToken<List<Answer>>() {
        }.getType();
        List<Answer> answers = modelMapper.map(answerDTOs, answerListType);

        MappingAssertions.assertAnswers(answers, answerDTOs);
    }

    @Test
    @DisplayName("User to UserDTO")
    void mapUserToUserDTO() {
        User user = new UserBuilder()
                .createUser(1L, "Test User");

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        MappingAssertions.assertUserDTO(userDTO, user);
    }

    @Test
    @DisplayName("UserDTO to User")
    void mapUserDTOToUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Test User");

        User user = modelMapper.map(userDTO, User.class);

        MappingAssertions.assertUser(user, userDTO);
    }

}
