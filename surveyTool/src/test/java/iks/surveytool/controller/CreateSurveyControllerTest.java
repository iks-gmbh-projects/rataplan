package iks.surveytool.controller;

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
    @DisplayName("Test survey as SessionAttribute")
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
    @DisplayName("Failed POST of Survey - End date in past - Check if SessionAttribute present in next GET-mapping")
    void postSurveyFailure_DateInPast_CheckIfNextGetMappingContainsSurveySessionAttribute() throws Exception {
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
}