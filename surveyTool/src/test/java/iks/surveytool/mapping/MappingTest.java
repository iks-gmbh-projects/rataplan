package iks.surveytool.mapping;

import iks.surveytool.repositories.SurveyRepository;
import iks.surveytool.repositories.SurveyResponseRepository;
import iks.surveytool.services.CryptoServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testing Mapper")
public class MappingTest {
    @Mock
    private CryptoServiceImpl cryptoService;
    @Mock
    private SurveyRepository surveyRepository;
    @Mock
    private SurveyResponseRepository surveyResponseRepository;
    private ModelMapper modelMapper;
    
    @BeforeEach
    void setup() {
    
    }

//    @Test
//    @DisplayName("Survey to CompleteSurveyDTO")
//    void mapSurveyToDTO() {
//        Checkbox firstCheckbox = new ChoiceBuilder()
//            .createChoice(1L, "First Test Checkbox", false);
//        Checkbox secondCheckbox = new ChoiceBuilder()
//            .createChoice(2L, "Second Test Checkbox", true);
//        Checkbox thirdCheckbox = new ChoiceBuilder()
//            .createChoice(3L, "Third Test Checkbox", true);
//        Checkbox fourthCheckbox = new ChoiceBuilder()
//            .createChoice(4L, "Fourth Test Checkbox", false);
//
//        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
//            .createCheckboxGroup(1L, true, 1, 3);
//        checkboxGroup.setCheckboxes(List.of(firstCheckbox, secondCheckbox, thirdCheckbox, fourthCheckbox));
//
//        Question question = new QuestionBuilder()
//            .createQuestion(1L, "Test Question", false, true);
//        question.setCheckboxGroup(checkboxGroup);
//
//        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
//            .createQuestionGroup(1L, "QuestionGroup with Question");
//        questionGroupWithQuestion.setQuestions(List.of(question));
//
//        Survey survey = new SurveyBuilder()
//            .createSurveyWithUserAndDefaultDate(1L, "Complete Survey", 1L);
//        survey.setQuestionGroups(List.of(questionGroupWithQuestion));
//
//        CompleteSurveyDTO surveyDTO = modelMapper.map(survey, CompleteSurveyDTO.class);
//
//        MappingAssertions.assertCompleteSurveyDTO(surveyDTO, survey);
//    }
//
//    @Test
//    @DisplayName("CompleteSurveyDTO to Survey")
//    void mapSurveyDTOtoEntity() {
//        CheckboxDTO firstCheckboxDTO = new CheckboxDTO();
//        firstCheckboxDTO.setId(1L);
//        firstCheckboxDTO.setText("First Checkbox DTO");
//        firstCheckboxDTO.setHasTextField(false);
//        CheckboxDTO secondCheckboxDTO = new CheckboxDTO();
//        secondCheckboxDTO.setId(2L);
//        secondCheckboxDTO.setText("Second Checkbox DTO");
//        secondCheckboxDTO.setHasTextField(true);
//
//        CheckboxGroupDTO checkboxGroupDTO = new CheckboxGroupDTO();
//        checkboxGroupDTO.setId(1L);
//        checkboxGroupDTO.setMultipleSelect(false);
//        checkboxGroupDTO.setCheckboxes(List.of(firstCheckboxDTO, secondCheckboxDTO));
//
//        QuestionDTO firstQuestionDTO = new QuestionDTO();
//        firstQuestionDTO.setId(1L);
//        firstQuestionDTO.setText("Test Checkbox Question");
//        firstQuestionDTO.setRequired(false);
//        firstQuestionDTO.setHasCheckbox(true);
//        firstQuestionDTO.setCheckboxGroup(checkboxGroupDTO);
//
//        QuestionDTO secondQuestionDTO = new QuestionDTO();
//        secondQuestionDTO.setId(1L);
//        secondQuestionDTO.setText("Test Text Question");
//        secondQuestionDTO.setRequired(true);
//        secondQuestionDTO.setHasCheckbox(false);
//
//        QuestionGroupDTO questionGroupDTO = new QuestionGroupDTO();
//        questionGroupDTO.setId(1L);
//        questionGroupDTO.setTitle("Test QuestionGroup");
//        questionGroupDTO.setQuestions(List.of(firstQuestionDTO, secondQuestionDTO));
//
//        CompleteSurveyDTO surveyDTO = new CompleteSurveyDTO();
//        surveyDTO.setId(1L);
//        surveyDTO.setName("Test Survey");
//        ZonedDateTime startDate = ZonedDateTime.of(2050, 1, 1, 12, 0, 0, 0, ZoneId.systemDefault());
//        surveyDTO.setStartDate(startDate.toInstant());
//        surveyDTO.setEndDate(startDate.plusWeeks(1L).toInstant());
//        surveyDTO.setOpenAccess(false);
//        surveyDTO.setAnonymousParticipation(true);
//        surveyDTO.setAccessId("Dummy String");
//        surveyDTO.setParticipationId("Dummy String");
//        surveyDTO.setUserId(1L);
//        surveyDTO.setQuestionGroups(List.of(questionGroupDTO));
//
//        Survey surveyConverted = modelMapper.map(surveyDTO, Survey.class);
//
//        System.out.println(surveyConverted.getUserId());
//
//        MappingAssertions.assertSurvey(surveyConverted, surveyDTO);
//    }
//
//    @Test
//    @DisplayName("AnswerList to AnswerDTOList")
//    void mapAnswerListToAnswerDTOs() {
//        Survey survey = new SurveyBuilder()
//            .createDefaultSurvey();
//        Checkbox firstCheckbox = new ChoiceBuilder()
//            .createChoice(1L, "First Test Checkbox", false);
//        Checkbox secondCheckbox = new ChoiceBuilder()
//            .createChoice(2L, "Second Test Checkbox", true);
//
//        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
//            .createCheckboxGroup(1L, false, 0, 2);
//        checkboxGroup.setCheckboxes(List.of(firstCheckbox, secondCheckbox));
//
//        QuestionGroup group = new QuestionGroupBuilder()
//            .createQuestionGroupIn(survey, 1L, "Bla");
//
//        Question firstQuestion = new QuestionBuilder()
//            .createQuestionIn(group, 1L, "Test Question", false, false);
//        Question secondQuestion = new QuestionBuilder()
//            .createQuestionIn(group, 2L, "Test Question", false, true);
//        secondQuestion.setCheckboxGroup(checkboxGroup);
//
//        SurveyResponse response = new SurveyResponseBuilder()
//            .createResponse(1L, survey, null);
//
//        new AnswerBuilder()
//            .createAnswerIn(response, 1L, "Test Answer", firstQuestion, null);
//        new AnswerBuilder()
//            .createAnswerIn(response, 2L, null, secondQuestion, List.of(firstCheckbox));
//
//        SurveyResponseDTO responseDTO = modelMapper.map(response, SurveyResponseDTO.class);
//
//        MappingAssertions.assertSurveyResponseDTO(responseDTO, response);
//    }
//
//    @Test
//    @DisplayName("AnswerDTOList to AnswerList")
//    void mapAnswerDTOsToAnswers() {
//        AnswerDTO firstAnswerDTO = new AnswerDTO();
//        firstAnswerDTO.setId(1L);
//        firstAnswerDTO.setText("Text");
//        AnswerDTO secondAnswerDTO = new AnswerDTO();
//        secondAnswerDTO.setId(2L);
//        secondAnswerDTO.setCheckboxes(Map.of(1L, true));
//
//        SurveyResponseDTO surveyResponseDTO = new SurveyResponseDTO();
//        surveyResponseDTO.setAnswers(Map.of(
//            firstAnswerDTO.getId(), firstAnswerDTO,
//            secondAnswerDTO.getId(), secondAnswerDTO
//        ));
//
//        SurveyResponse surveyResponse = modelMapper.map(surveyResponseDTO, SurveyResponse.class);
//
//        MappingAssertions.assertSurveyResponse(surveyResponse, surveyResponseDTO);
//    }
}