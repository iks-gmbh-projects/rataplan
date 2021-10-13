package iks.surveytool.controller;

import iks.surveytool.builder.QuestionBuilder;
import iks.surveytool.builder.SurveyBuilder;
import iks.surveytool.entities.Question;
import iks.surveytool.entities.Survey;
import iks.surveytool.services.SurveyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.FlashMap;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest
@DisplayName("Testing CreateSurveyController")
class CreateSurveyControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SurveyService surveyService;

    @Test
    @DisplayName("Testing getSurveyForm()")
    void getSurveyForm() throws Exception {
        mvc.perform(get("/createSurvey"))
                .andExpect(status().isOk())
                .andExpect(view().name("createSurvey"));
    }

    @Test
    @DisplayName("Testing survey as SessionAttribute")
    void getSurveyFormSessionAttribute() throws Exception {
        MvcResult result = mvc.perform(get("/createSurvey"))
                .andExpect(model().attributeExists("survey"))
                .andReturn();

        Survey survey = (Survey) Objects.requireNonNull(result.getModelAndView()).getModel().get("survey");

        assertNull(survey.getName());
    }

    @Test
    @DisplayName("Successful POST of Survey - Check if SessionAttribute present")
    void postSurveySuccess_CheckIfNextGetMappingContainsSurveySessionAttribute() throws Exception {
        LocalDateTime startDate = LocalDateTime.of(3000, 9, 1, 13, 0);
        // endDate is in distant future
        LocalDateTime endDate = LocalDateTime.of(3000, 10, 1, 13, 0);

        FlashMap flashMap = mvc.perform(post("/createSurvey")
                        .param("name", "Test Umfrage")
                        .param("startDate", String.valueOf(startDate))
                        .param("endDate", String.valueOf(endDate)))
                .andExpect(status().is3xxRedirection())
                .andReturn().getFlashMap();

        MvcResult result = mvc.perform(get("/createSurvey/questions")
                        .sessionAttrs(flashMap))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("survey"))
                .andReturn();

        Survey survey = (Survey) Objects.requireNonNull(result.getModelAndView()).getModel().get("survey");

        assertEquals("Test Umfrage", survey.getName());
    }

    @Test
    @DisplayName("Failed POST of Survey - End date in past - Check if SessionAttribute not present in next GET-mapping")
    void postSurveyFailure_DateInPast_CheckIfNextGetMappingDoesNotContainSurveySessionAttribute() throws Exception {
        LocalDateTime startDate = LocalDateTime.of(1000, 10, 1, 13, 0);
        // endDate is in past
        LocalDateTime endDate = LocalDateTime.of(1000, 11, 1, 13, 0);

        FlashMap flashMap = mvc.perform(post("/createSurvey")
                        .param("name", "Test Umfrage")
                        .param("startDate", String.valueOf(startDate))
                        .param("endDate", String.valueOf(endDate)))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andReturn().getFlashMap();

        assertTrue(flashMap.isEmpty());
    }

    @Test
    @DisplayName("Testing getAddQuestionsForm()")
    void getAddQuestionsForm() throws Exception {
        mvc.perform(get("/createSurvey/questions"))
                .andExpect(status().isOk())
                .andExpect(view().name("addQuestions"));
    }

    @Test
    @DisplayName("Successfully adding QuestionGroup - Check if SessionAttribute present")
    void addQuestionGroup_CheckIfNextGetMappingContainsSurveySessionAttribute() throws Exception {
        Survey defaultSurvey = new SurveyBuilder()
                .setName("Test Survey - Add QuestionGroup")
                .build();

        FlashMap flashMap = mvc.perform(post("/createSurvey/questions/addGroup")
                        .sessionAttr("survey", defaultSurvey)
                        .param("title", "Test QuestionGroup"))
                .andExpect(status().is3xxRedirection())
                .andReturn().getFlashMap();

        MvcResult result = mvc.perform(get("/createSurvey/questions")
                        .sessionAttrs(flashMap))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("survey"))
                .andReturn();

        Survey survey = (Survey) Objects.requireNonNull(result.getModelAndView()).getModel().get("survey");

        assertEquals("Test Survey - Add QuestionGroup", survey.getName());
    }

    @Test
    @DisplayName("Successfully adding Question - No CheckboxGroup - Check if SessionAttribute present")
    void addQuestionNoCheckboxes_CheckIfNextGetMappingContainsSurveySessionAttribute() throws Exception {
        Survey defaultSurveyWithQuestionGroup = new SurveyBuilder()
                .setName("Test Survey - Add Question (No Checkboxes)")
                .addQuestionGroup("Test Group")
                .build();

        FlashMap flashMap = mvc.perform(post("/createSurvey/questions/addQuestion/0")
                        .sessionAttr("survey", defaultSurveyWithQuestionGroup)
                        .param("text", "Test test?")
                        .param("required", String.valueOf(true))
                        .param("hasCheckbox", String.valueOf(false)))
                .andExpect(status().is3xxRedirection())
                .andReturn().getFlashMap();

        MvcResult result = mvc.perform(get("/createSurvey/questions")
                        .sessionAttrs(flashMap))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("survey"))
                .andReturn();

        Survey survey = (Survey) Objects.requireNonNull(result.getModelAndView()).getModel().get("survey");

        assertEquals("Test Survey - Add Question (No Checkboxes)", survey.getName());
    }

    @Test
    @DisplayName("Successfully adding Question - With CheckboxGroup - Check if SessionAttribute present")
    void addQuestionWithCheckboxes_CheckIfNextGetMappingContainsSurveySessionAttribute() throws Exception {
        Survey defaultSurveyWithQuestionGroup = new SurveyBuilder()
                .setName("Test Survey - Add Question (With Checkboxes)")
                .addQuestionGroup("Test Group")
                .build();

        FlashMap flashMap = mvc.perform(post("/createSurvey/questions/addQuestion/0")
                        .sessionAttr("survey", defaultSurveyWithQuestionGroup)
                        .param("text", "Test test Checkbox?")
                        .param("required", String.valueOf(true))
                        .param("hasCheckbox", String.valueOf(true))
                        .param("multipleSelect", String.valueOf(true))
                        .param("minSelect", String.valueOf(2))
                        .param("maxSelect", String.valueOf(4)))
                .andExpect(status().is3xxRedirection())
                .andReturn().getFlashMap();

        MvcResult result = mvc.perform(get("/createSurvey/questions")
                        .sessionAttrs(flashMap))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("survey"))
                .andReturn();

        Survey survey = (Survey) Objects.requireNonNull(result.getModelAndView()).getModel().get("survey");

        assertEquals("Test Survey - Add Question (With Checkboxes)", survey.getName());
    }

    @Test
    @DisplayName("Successfully adding Checkbox - Check if SessionAttribute present")
    void addCheckbox_CheckIfNextGetMappingContainsSurveySessionAttribute() throws Exception {
        Question defaultQuestionWithCheckbox = new QuestionBuilder()
                .setHasCheckbox(true)
                .build();

        Survey defaultSurveyWithQuestionGroup = new SurveyBuilder()
                .setName("Test Survey - Add Question (With Checkboxes)")
                .addQuestionGroup("Test Group")
                .addQuestionToGroup(defaultQuestionWithCheckbox, 0)
                .build();

        FlashMap flashMap = mvc.perform(post("/createSurvey/questions/addQuestion/0/0")
                        .sessionAttr("survey", defaultSurveyWithQuestionGroup)
                        .param("text", "New test checkbox")
                        .param("hasTextField", String.valueOf(false)))
                .andExpect(status().is3xxRedirection())
                .andReturn().getFlashMap();

        MvcResult result = mvc.perform(get("/createSurvey/questions")
                        .sessionAttrs(flashMap))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("survey"))
                .andReturn();

        Survey survey = (Survey) Objects.requireNonNull(result.getModelAndView()).getModel().get("survey");

        assertEquals("Test Survey - Add Question (With Checkboxes)", survey.getName());
    }
}