package iks.surveytool.controller;

import iks.surveytool.services.SurveyService;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@DisplayName("Testing AnswerController")
class AnswerControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SurveyService surveyService;

}