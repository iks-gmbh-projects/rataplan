package iks.surveytool.utils.builder;

import iks.surveytool.entities.EncryptedString;
import iks.surveytool.entities.Survey;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class SurveyBuilder {

    private static final EncryptedString description = new EncryptedString("Default Beschreibung", true);
    private static final ZonedDateTime startDate = ZonedDateTime.of(2050, 1, 1, 12, 0, 0, 0, ZoneId.systemDefault());
    private static final ZonedDateTime endDate = startDate.plusWeeks(1L);
    private static final boolean openAccess = false;
    private static final boolean anonymousParticipation = false;
    private static final String accessId = "Test ID";

    public static Survey createDefaultSurvey() {
        Survey newSurvey = new Survey();
        newSurvey.setId(1L);
        newSurvey.setName(new EncryptedString("Test Survey", true));
        newSurvey.setUserId(1L);
        setDefaults(newSurvey);
        return newSurvey;
    }

    public static Survey createSurveyWithDefaultDate(Long id,
                                              String name) {
        Survey newSurvey = new Survey();
        newSurvey.setId(id);
        newSurvey.setName(name == null ? null : new EncryptedString(name, true));
        setDefaults(newSurvey);
        return newSurvey;
    }

    public static Survey createSurveyWithUserAndDefaultDate(Long id,
                                                     String name,
                                                     Long userId) {
        Survey newSurvey = new Survey();
        newSurvey.setId(id);
        newSurvey.setName(name == null ? null : new EncryptedString(name, true));
        newSurvey.setUserId(userId);
        setDefaults(newSurvey);
        return newSurvey;
    }

    private static void setDefaults(Survey newSurvey) {
        newSurvey.setDescription(description);
        newSurvey.setStartDate(startDate);
        newSurvey.setEndDate(endDate);
        newSurvey.setOpenAccess(openAccess);
        newSurvey.setAccessId(accessId);
        newSurvey.setAnonymousParticipation(anonymousParticipation);
        newSurvey.setQuestionGroups(new ArrayList<>());
    }
}