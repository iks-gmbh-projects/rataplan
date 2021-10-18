package iks.surveytool.controller;

import iks.surveytool.builder.SurveyBuilder;
import iks.surveytool.entities.*;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest
@DisplayName("Testing CreateSurveyController")
class SurveyControllerTest {

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
                .andExpect(model().attributeExists("newSurvey"))
                .andReturn();

        Survey newSurvey = (Survey) Objects.requireNonNull(result.getModelAndView()).getModel().get("newSurvey");

        assertNull(newSurvey.getName());
    }

    @Test
    @DisplayName("Successful POST of Survey - Check if SessionAttribute present")
    void postSurveySuccess_CheckIfNextGetMappingContainsSurveySessionAttribute() throws Exception {
        LocalDateTime startDate = LocalDateTime.of(3000, 9, 1, 13, 0);
        // endDate is in distant future
        LocalDateTime endDate = LocalDateTime.of(3000, 10, 1, 13, 0);

        when(surveyService.startDateBeforeEndDate(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(true);

        FlashMap flashMap = mvc.perform(post("/createSurvey")
                        .param("name", "Test Umfrage")
                        .param("startDate", String.valueOf(startDate))
                        .param("endDate", String.valueOf(endDate)))
                .andExpect(status().is3xxRedirection())
                .andReturn().getFlashMap();

        MvcResult result = mvc.perform(get("/createSurvey/questions")
                        .sessionAttrs(flashMap))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("newSurvey"))
                .andReturn();

        Survey newSurvey = (Survey) Objects.requireNonNull(result.getModelAndView()).getModel().get("newSurvey");

        assertEquals("Test Umfrage", newSurvey.getName());
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
                .andExpect(model().attributeHasFieldErrorCode("newSurvey", "endDate", "Future"))
                .andReturn().getFlashMap();

        assertTrue(flashMap.isEmpty());
    }

    @Test
    @DisplayName("Failed POST of Survey - endDate is before startDate - Check if SessionAttribute not present in next GET-mapping")
    void postSurveyFailure_EndBeforeStart_CheckIfNextGetMappingDoesNotContainSurveySessionAttribute() throws Exception {
        LocalDateTime startDate = LocalDateTime.of(3000, 10, 1, 13, 0);
        // endDate is in past
        LocalDateTime endDate = LocalDateTime.of(3000, 9, 1, 13, 0);

        when(surveyService.startDateBeforeEndDate(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(false);

        FlashMap flashMap = mvc.perform(post("/createSurvey")
                        .param("name", "Test Umfrage")
                        .param("startDate", String.valueOf(startDate))
                        .param("endDate", String.valueOf(endDate)))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrorCode("newSurvey", "endDate", "error.survey.endDate"))
                .andReturn().getFlashMap();

        assertTrue(flashMap.isEmpty());
    }


    @Test
    @DisplayName("Successfully adding QuestionGroup - Check if SessionAttribute present")
    void addQuestionGroup_CheckIfNextGetMappingContainsSurveySessionAttribute() throws Exception {
        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Test Survey - Add QuestionGroup");

        FlashMap flashMap = mvc.perform(post("/createSurvey/questions/addGroup")
                        .sessionAttr("newSurvey", survey)
                        .param("title", "Test QuestionGroup"))
                .andExpect(status().is3xxRedirection())
                .andReturn().getFlashMap();

        verify(surveyService, times(1))
                .addQuestionGroupToSurvey(eq(survey), any(QuestionGroup.class));

        MvcResult result = mvc.perform(get("/createSurvey/questions")
                        .sessionAttrs(flashMap))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("newSurvey"))
                .andReturn();
        Survey newSurvey = (Survey) Objects.requireNonNull(result.getModelAndView()).getModel().get("newSurvey");

        assertEquals("Test Survey - Add QuestionGroup", newSurvey.getName());
    }

    @Test
    @DisplayName("Successfully adding Question - No CheckboxGroup - Check if SessionAttribute present")
    void addQuestionNoCheckboxes_CheckIfNextGetMappingContainsSurveySessionAttribute() throws Exception {
        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Test Survey - Add Question (No Checkboxes)");

        FlashMap flashMap = mvc.perform(post("/createSurvey/questions/addQuestion/0")
                        .sessionAttr("newSurvey", survey)
                        .param("text", "Test test?")
                        .param("required", String.valueOf(true))
                        .param("hasCheckbox", String.valueOf(false)))
                .andExpect(status().is3xxRedirection())
                .andReturn().getFlashMap();

        verify(surveyService, times(1))
                .addQuestionToQuestionGroup(eq(survey),
                        eq(0),
                        any(Question.class),
                        any(CheckboxGroup.class));

        MvcResult result = mvc.perform(get("/createSurvey/questions")
                        .sessionAttrs(flashMap))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("newSurvey"))
                .andReturn();

        Survey newSurvey = (Survey) Objects.requireNonNull(result.getModelAndView()).getModel().get("newSurvey");

        assertEquals("Test Survey - Add Question (No Checkboxes)", newSurvey.getName());
    }

    @Test
    @DisplayName("Successfully adding Question - With CheckboxGroup - Check if SessionAttribute present")
    void addQuestionWithCheckboxes_CheckIfNextGetMappingContainsSurveySessionAttribute() throws Exception {
        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Test Survey - Add Question (With Checkboxes)");

        FlashMap flashMap = mvc.perform(post("/createSurvey/questions/addQuestion/0")
                        .sessionAttr("newSurvey", survey)
                        .param("text", "Test test Checkbox?")
                        .param("required", String.valueOf(true))
                        .param("hasCheckbox", String.valueOf(true))
                        .param("multipleSelect", String.valueOf(true))
                        .param("minSelect", String.valueOf(2))
                        .param("maxSelect", String.valueOf(4)))
                .andExpect(status().is3xxRedirection())
                .andReturn().getFlashMap();

        verify(surveyService, times(1))
                .addQuestionToQuestionGroup(eq(survey),
                        eq(0),
                        any(Question.class),
                        any(CheckboxGroup.class));

        MvcResult result = mvc.perform(get("/createSurvey/questions")
                        .sessionAttrs(flashMap))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("newSurvey"))
                .andReturn();

        Survey newSurvey = (Survey) Objects.requireNonNull(result.getModelAndView()).getModel().get("newSurvey");

        assertEquals("Test Survey - Add Question (With Checkboxes)", newSurvey.getName());
    }

    @Test
    @DisplayName("Successfully adding Checkbox - Check if SessionAttribute present")
    void addCheckbox_CheckIfNextGetMappingContainsSurveySessionAttribute() throws Exception {
        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Test Survey - Add Checkboxes");

        FlashMap flashMap = mvc.perform(post("/createSurvey/questions/addQuestion/0/0")
                        .sessionAttr("newSurvey", survey)
                        .param("text", "New test checkbox")
                        .param("hasTextField", String.valueOf(false)))
                .andExpect(status().is3xxRedirection())
                .andReturn().getFlashMap();

        verify(surveyService, times(1))
                .addCheckboxToQuestion(eq(survey),
                        eq(0),
                        eq(0),
                        any(Checkbox.class));

        MvcResult result = mvc.perform(get("/createSurvey/questions")
                        .sessionAttrs(flashMap))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("newSurvey"))
                .andReturn();

        Survey newSurvey = (Survey) Objects.requireNonNull(result.getModelAndView()).getModel().get("newSurvey");

        assertEquals("Test Survey - Add Checkboxes", newSurvey.getName());
    }

    @Test
    @DisplayName("Failed saving of survey - no QuestionGroups")
    void failedSavingOfSurveyNoQuestiongroups_CheckIfNextGetMappingContainsSurveySessionAttribute() throws Exception {
        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Test Survey - Save");

        when(surveyService.checkIfAnythingEmpty(survey))
                .thenReturn(List.of("Eine Umfrage muss aus mind. einem Frageblock bestehen."));

        FlashMap flashMap = mvc.perform(post("/createSurvey/save")
                        .sessionAttr("newSurvey", survey))
                .andExpect(status().isOk())
                .andExpect(view().name("addQuestions"))
                .andReturn().getFlashMap();

        assertTrue(flashMap.isEmpty());
    }

    @Test
    @DisplayName("Successfully save survey")
    void successfulSavingOfSurvey() throws Exception {
        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Test Survey - Save");

        when(surveyService.checkIfAnythingEmpty(survey))
                .thenReturn(List.of());

        FlashMap flashMap = mvc.perform(post("/createSurvey/save")
                        .sessionAttr("newSurvey", survey))
                .andExpect(status().is3xxRedirection())
                .andReturn().getFlashMap();

        assertTrue(flashMap.isEmpty());
    }

    @Test
    @DisplayName("Successful fetching of Survey")
    void getFinalOverviewOfSurvey_Successful() throws Exception {
        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Test Survey - Save");

        when(surveyService.findSurveyById(any(Long.class))).thenReturn(java.util.Optional.of(survey));

        mvc.perform(get("/createSurvey/1/final"))
                .andExpect(status().isOk())
                .andExpect(view().name("finalizeSurvey"))
                .andExpect(model().attributeExists("survey"))
                .andExpect(model().attributeExists("url"))
                .andExpect(model().attributeDoesNotExist("notAvailable"));
    }

    @Test
    @DisplayName("Failed fetching of Survey")
    void getFinalOverviewOfSurvey_Failed() throws Exception {
        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Test Survey - Save");

        when(surveyService.findSurveyById(any(Long.class))).thenReturn(Optional.empty());

        mvc.perform(get("/createSurvey/1/final"))
                .andExpect(status().isOk())
                .andExpect(view().name("finalizeSurvey"))
                .andExpect(model().attributeExists("notAvailable"))
                .andExpect(model().attributeDoesNotExist("survey"))
                .andExpect(model().attributeDoesNotExist("url"));
    }
}