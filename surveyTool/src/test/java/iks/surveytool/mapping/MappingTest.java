package iks.surveytool.mapping;

import iks.surveytool.dtos.CompleteSurveyDTO;
import iks.surveytool.entities.*;
import iks.surveytool.utils.assertions.CustomAssert;
import iks.surveytool.utils.builder.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

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

//        CompleteSurveyDTO surveyDTO = (CompleteSurveyDTO) mapper.toSurveyDto(survey, true);
        CompleteSurveyDTO surveyDTO = modelMapper.map(survey, CompleteSurveyDTO.class);

        CustomAssert.assertCompleteSurveyDTO(surveyDTO, survey);
    }

    @Test
    @DisplayName("CompleteSurveyDTO to Survey")
    void mapSurveyDTOtoEntity() {
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

        // TODO: Create DTO manually
        CompleteSurveyDTO surveyDTO = modelMapper.map(survey, CompleteSurveyDTO.class);
        System.out.println(surveyDTO.getUserId());
        System.out.println(surveyDTO.getUserName());
        //        Survey surveyConverted = mapper.createSurveyFromDto(surveyDTO);
        Survey surveyConverted = modelMapper.map(surveyDTO, Survey.class);
        System.out.println(surveyConverted.getUser().getId());
        System.out.println(surveyConverted.getUser().getName());

        CustomAssert.assertSurvey(surveyConverted, surveyDTO);
    }

}
