package iks.surveytool.services;

import iks.surveytool.builder.SurveyBuilder;
import iks.surveytool.entities.Survey;
import iks.surveytool.repositories.SurveyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testing SurveyService")
class SurveyServiceTest {

    @Mock
    private SurveyRepository surveyRepository;

    @InjectMocks
    private SurveyService surveyService;

    @Test
    @DisplayName("Adding Survey")
    void addSurvey() {
        Survey survey = new SurveyBuilder()
                .createSurveyWithDefaultDate(1L, "Test Umfrage");
        when(surveyRepository.save(survey)).thenReturn(survey);

        surveyService.saveSurvey(survey);

        verify(surveyRepository, times(2)).save(survey);
    }
}