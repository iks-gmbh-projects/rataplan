package iks.surveytool.controller;

import iks.surveytool.builder.SurveyBuilder;
import iks.surveytool.entities.Survey;
import iks.surveytool.services.SurveyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@DisplayName("Testing AnswerController")
class AnswerControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SurveyService surveyService;

    @Test
    @DisplayName("Get Survey - Successful")
    void getSurvey_Successful() throws Exception {
        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Test Survey");

        when(surveyService.findSurveyByUUID(any(UUID.class))).thenReturn(java.util.Optional.of(survey));

        mvc.perform(get("/answer")
                        .param("surveyUUID", String.valueOf(UUID.randomUUID())))
                .andExpect(status().isOk())
                .andExpect(view().name("answerSurvey"))
                .andExpect(model().attributeExists("survey"))
                .andExpect(model().attributeDoesNotExist("notAvailable"));
    }

    @Test
    @DisplayName("Get Survey - Failed")
    void getSurvey_Failed() throws Exception {
        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Test Survey");

        when(surveyService.findSurveyByUUID(any(UUID.class))).thenReturn(Optional.empty());

        mvc.perform(get("/answer")
                        .param("surveyUUID", String.valueOf(UUID.randomUUID())))
                .andExpect(status().isOk())
                .andExpect(view().name("answerSurvey"))
                .andExpect(model().attributeExists("notAvailable"))
                .andExpect(model().attributeDoesNotExist("survey"));
    }
}