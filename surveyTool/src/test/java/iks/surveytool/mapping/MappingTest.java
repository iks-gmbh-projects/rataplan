package iks.surveytool.mapping;

import iks.surveytool.config.MappingConfig;
import iks.surveytool.dtos.*;
import iks.surveytool.entities.QuestionGroup;
import iks.surveytool.entities.Survey;
import iks.surveytool.entities.SurveyResponse;
import iks.surveytool.entities.question.*;
import iks.surveytool.mapping.crypto.FromRawEncryptedStringConverter;
import iks.surveytool.mapping.crypto.ToRawEncryptedStringConverter;
import iks.surveytool.repositories.SurveyRepository;
import iks.surveytool.services.CryptoService;
import iks.surveytool.utils.builder.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testing Mapper")
public class MappingTest {
    @Mock
    private CryptoService cryptoService;
    @Mock
    private SurveyRepository surveyRepository;
    private ModelMapper modelMapper;
    
    @BeforeEach
    void setup() {
        lenient().when(cryptoService.decryptDB(anyString())).then(a -> a.getArgument(0));
        lenient().when(cryptoService.encryptDB(anyString())).then(a -> a.getArgument(0));
        lenient().when(cryptoService.decryptDBRaw(any(byte[].class)))
            .then(a -> new String(a.getArgument(0), StandardCharsets.UTF_8));
        lenient().when(cryptoService.encryptDBRaw(anyString()))
            .then(a -> a.getArgument(0, String.class).getBytes(StandardCharsets.UTF_8));
        modelMapper = MappingConfig.modelMapper(List.of(new FromRawEncryptedStringConverter(cryptoService),
            new ToRawEncryptedStringConverter(cryptoService),
            new ToInstantConverter(),
            new ToZonedTimeConverter(),
            new ListConverter<>(),
            new SurveyResponseToDTOConverter(),
            new SurveyResponseFromDTOConverter(surveyRepository),
            new QuestionGroupFromDTOConverter(),
            new QuestionGroupToDTOConverter()
        ));
    }
    
    @Test
    @DisplayName("Survey to CompleteSurveyDTO")
    void mapSurveyToDTO() {
        Survey survey = SurveyBuilder.createSurveyWithUserAndDefaultDate(1L, "Complete Survey", 1L);
        
        QuestionGroup questionGroupWithQuestion = QuestionGroupBuilder.createQuestionGroupIn(
            survey,
            1L,
            "QuestionGroup with Question"
        );
        
        ChoiceQuestion choiceQuestion = QuestionBuilder.createQuestionIn(
            questionGroupWithQuestion,
            1L,
            0,
            "Test Question",
            1,
            3
        );
        
        ChoiceBuilder.createChoiceIn(choiceQuestion, 1L, "First Test Checkbox", false);
        ChoiceBuilder.createChoiceIn(choiceQuestion, 2L, "Second Test Checkbox", true);
        ChoiceBuilder.createChoiceIn(choiceQuestion, 3L, "Third Test Checkbox", true);
        ChoiceBuilder.createChoiceIn(choiceQuestion, 4L, "Fourth Test Checkbox", false);
        
        QuestionBuilder.createQuestionIn(questionGroupWithQuestion, 1L, 1, "Test Question 2", true);
        
        OrderQuestion orderQuestion = QuestionBuilder.createQuestionIn(
            questionGroupWithQuestion,
            1L,
            2,
            "Test Question 3"
        );
        
        ChoiceBuilder.createChoiceIn(orderQuestion, 1L, "alpha");
        ChoiceBuilder.createChoiceIn(orderQuestion, 2L, "delta");
        ChoiceBuilder.createChoiceIn(orderQuestion, 3L, "beta");
        ChoiceBuilder.createChoiceIn(orderQuestion, 4L, "gamma");
        
        CompleteSurveyDTO surveyDTO = modelMapper.map(survey, CompleteSurveyDTO.class);
        
        survey.resetId();
        
        assertEquals(survey, modelMapper.map(surveyDTO, Survey.class));
    }
    
    @Test
    @DisplayName("CompleteSurveyDTO to Survey")
    void mapSurveyDTOtoEntity() {
        
        QuestionGroupDTO questionGroupDTO = QuestionGroupDTO.builder()
            .id(1L)
            .title("Test QuestionGroup")
            .question(new QuestionDTO(1L,
                "Test Checkbox Question",
                0,
                1,
                ChoiceDTO.builder()
                    .id(1L)
                    .text("First Checkbox DTO")
                    .hasTextField(false)
                    .build(),
                ChoiceDTO.builder()
                    .id(2L)
                    .text("Second Checkbox DTO")
                    .hasTextField(true)
                    .build()
            ))
            .question(new QuestionDTO(
                1L,
                "Test Text Question",
                true
            ))
            .question(new QuestionDTO(
                1L,
                "Test Order Question",
                ChoiceDTO.builder()
                    .id(1L)
                    .text("Order 2")
                    .build(),
                ChoiceDTO.builder()
                    .id(2L)
                    .text("Order 1")
                    .build(),
                ChoiceDTO.builder()
                    .id(3L)
                    .text("Order 3")
                    .build(),
                ChoiceDTO.builder()
                    .id(4L)
                    .text("Order 4")
                    .build()
            ))
            .build();
        
        CompleteSurveyDTO surveyDTO = new CompleteSurveyDTO();
        surveyDTO.setId(1L);
        surveyDTO.setName("Test Survey");
        ZonedDateTime startDate = ZonedDateTime.of(2050, 1, 1, 12, 0, 0, 0, ZoneId.systemDefault());
        surveyDTO.setStartDate(startDate.toInstant());
        surveyDTO.setEndDate(startDate.plusWeeks(1L).toInstant());
        surveyDTO.setOpenAccess(false);
        surveyDTO.setAnonymousParticipation(true);
        surveyDTO.setAccessId("Dummy String");
        surveyDTO.setParticipationId("Dummy String");
        surveyDTO.setUserId(1L);
        surveyDTO.setQuestionGroups(List.of(questionGroupDTO));
        
        Survey surveyConverted = modelMapper.map(surveyDTO, Survey.class);
        
        surveyDTO.resetId();
        
        assertEquals(surveyDTO, modelMapper.map(surveyConverted, CompleteSurveyDTO.class));
    }
    
    @Test
    @DisplayName("AnswerList to AnswerDTOList")
    void mapAnswerListToAnswerDTOs() {
        Survey survey = SurveyBuilder.createDefaultSurvey();
        
        QuestionGroup questionGroup = QuestionGroupBuilder.createQuestionGroupIn(survey, 1L, "Bla");
        
        OpenQuestion firstQuestion = QuestionBuilder.createQuestionIn(questionGroup, 1L, 0, "Test Question", false);
        ChoiceQuestion secondQuestion = QuestionBuilder.createQuestionIn(questionGroup, 1L, 1, "Test Question", 0, 2);
        
        ChoiceQuestionChoice firstCheckbox = ChoiceBuilder.createChoiceIn(
            secondQuestion,
            1L,
            "First Test Checkbox",
            false
        );
        ChoiceBuilder.createChoiceIn(secondQuestion, 2L, "Second Test Checkbox", true);
        
        OrderQuestion thirdQuestion = QuestionBuilder.createQuestionIn(questionGroup, 1L, 2, "Test Question");
        
        OrderQuestionChoice firstOrder = ChoiceBuilder.createChoiceIn(thirdQuestion, 1L, "First");
        OrderQuestionChoice secondOrder = ChoiceBuilder.createChoiceIn(thirdQuestion, 2L, "Second");
        OrderQuestionChoice thirdOrder = ChoiceBuilder.createChoiceIn(thirdQuestion, 3L, "Third");
        
        SurveyResponse response = SurveyResponseBuilder.createResponse(1L, survey, null);
        
        AnswerBuilder.createAnswerIn(response, 1L, "Test Answer", firstQuestion);
        response.getChoiceAnswers().add(firstCheckbox);
        response.getOrderAnswers().add(secondOrder);
        response.getOrderAnswers().add(thirdOrder);
        response.getOrderAnswers().add(firstOrder);
        
        SurveyResponseDTO responseDTO = modelMapper.map(response, SurveyResponseDTO.class);
        
        when(surveyRepository.findById(survey.getId())).thenReturn(Optional.of(survey));
        
        response.resetId();
        
        assertEquals(response, modelMapper.map(responseDTO, SurveyResponse.class));
    }
    
    @Test
    @DisplayName("AnswerDTOList to AnswerList")
    void mapAnswerDTOsToAnswers() {
        Survey survey = SurveyBuilder.createDefaultSurvey();
        
        QuestionGroup questionGroup = QuestionGroupBuilder.createQuestionGroupIn(survey, 1L, "Group");
        
        OpenQuestion firstQuestion = QuestionBuilder.createQuestionIn(questionGroup, 1L, 0, "Frage 1", true);
        ChoiceQuestion secondQuestion = QuestionBuilder.createQuestionIn(questionGroup, 2L, 1, "Frage 2", 1, 1);
        
        ChoiceBuilder.createChoiceIn(secondQuestion, 1L, "Option 1", false);
        ChoiceBuilder.createChoiceIn(secondQuestion, 2L, "Option 2", false);
        
        OrderQuestion thirdQuestion = QuestionBuilder.createQuestionIn(questionGroup, 1L, 2, "Test Question");
        
        ChoiceBuilder.createChoiceIn(thirdQuestion, 1L, "First");
        ChoiceBuilder.createChoiceIn(thirdQuestion, 2L, "Second");
        ChoiceBuilder.createChoiceIn(thirdQuestion, 3L, "Third");
        
        AnswerDTO firstAnswerDTO = new AnswerDTO("Text");
        AnswerDTO secondAnswerDTO = new AnswerDTO(Map.of(1L, true));
        AnswerDTO thirdAnswerDTO = new AnswerDTO(List.of(2L, 3L, 1L));
        
        SurveyResponseDTO surveyResponseDTO = new SurveyResponseDTO();
        surveyResponseDTO.setSurveyId(survey.getId());
        surveyResponseDTO.setAnswers(Map.of(
            questionGroup.getId(),
            Map.of(
                firstQuestion.getRank(), firstAnswerDTO,
                secondQuestion.getRank(), secondAnswerDTO,
                thirdQuestion.getRank(), thirdAnswerDTO
            )
        ));
        
        when(surveyRepository.findById(survey.getId())).thenReturn(Optional.of(survey));
        
        SurveyResponse surveyResponse = modelMapper.map(surveyResponseDTO, SurveyResponse.class);
        
        assertEquals(surveyResponseDTO, modelMapper.map(surveyResponse, SurveyResponseDTO.class));
    }
}