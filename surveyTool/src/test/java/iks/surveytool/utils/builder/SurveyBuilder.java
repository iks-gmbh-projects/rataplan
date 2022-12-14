package iks.surveytool.utils.builder;

import iks.surveytool.entities.EncryptedString;
import iks.surveytool.entities.Survey;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class SurveyBuilder {

    EncryptedString description = new EncryptedString("Default Beschreibung", false);
    ZonedDateTime startDate = ZonedDateTime.of(2050, 1, 1, 12, 0, 0, 0, ZoneId.systemDefault());
    ZonedDateTime endDate = startDate.plusWeeks(1L);
    boolean openAccess = false;
    boolean anonymousParticipation = false;
    String accessId = "Test ID";

    public Survey createDefaultSurvey() {
        Survey newSurvey = new Survey();
        newSurvey.setId(1L);
        newSurvey.setName(new EncryptedString("Test Survey", false));
        newSurvey.setUserId(1L);
        setDefaults(newSurvey);
        return newSurvey;
    }

    public Survey createSurveyWithDefaultDate(Long id,
                                              String name) {
        Survey newSurvey = new Survey();
        newSurvey.setId(id);
        newSurvey.setName(name == null ? null : new EncryptedString(name, false));
        setDefaults(newSurvey);
        return newSurvey;
    }

    public Survey createSurveyWithUserAndDefaultDate(Long id,
                                                     String name,
                                                     Long userId) {
        Survey newSurvey = new Survey();
        newSurvey.setId(id);
        newSurvey.setName(name == null ? null : new EncryptedString(name, false));
        newSurvey.setUserId(userId);
        setDefaults(newSurvey);
        return newSurvey;
    }

    private void setDefaults(Survey newSurvey) {
        newSurvey.setDescription(description);
        newSurvey.setStartDate(startDate);
        newSurvey.setEndDate(endDate);
        newSurvey.setOpenAccess(openAccess);
        newSurvey.setAccessId(accessId);
        newSurvey.setAnonymousParticipation(anonymousParticipation);
        newSurvey.setQuestionGroups(new ArrayList<>());
    }
}
