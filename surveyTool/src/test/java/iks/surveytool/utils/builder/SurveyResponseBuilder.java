package iks.surveytool.utils.builder;

import iks.surveytool.entities.Survey;
import iks.surveytool.entities.SurveyResponse;

import java.util.ArrayList;

public class SurveyResponseBuilder {
    public SurveyResponse createResponse(Long id, Survey survey, Long userId) {
        SurveyResponse newResponse = new SurveyResponse();
        newResponse.setId(id);
        newResponse.setSurvey(survey);
        newResponse.setUserId(userId);
        newResponse.setAnswers(new ArrayList<>());
        return newResponse;
    }
}
