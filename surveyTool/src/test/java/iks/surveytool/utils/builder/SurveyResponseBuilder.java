package iks.surveytool.utils.builder;

import iks.surveytool.entities.Survey;
import iks.surveytool.entities.SurveyResponse;

public class SurveyResponseBuilder {
    public SurveyResponse createResponse(Long id, Survey survey, Long userId) {
        SurveyResponse newResponse = new SurveyResponse();
        newResponse.setId(id);
        newResponse.setSurvey(survey);
        newResponse.setUserId(userId);
        return newResponse;
    }
}
