package iks.surveytool.utils.builder;

import iks.surveytool.entities.Survey;

import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;

public class SurveyBuilder {
    
    private static final byte[] description = "Default Beschreibung".getBytes(StandardCharsets.UTF_8);
    private static final ZonedDateTime startDate = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
    private static final ZonedDateTime endDate = startDate.plusWeeks(1L);
    private static final boolean openAccess = false;
    private static final boolean anonymousParticipation = false;
    private static final String accessId = "Test ID";
    
    public static Survey createDefaultSurvey() {
        Survey newSurvey = new Survey();
        newSurvey.setId(1L);
        newSurvey.setName("Test Survey".getBytes(StandardCharsets.UTF_8));
        newSurvey.setUserId(1L);
        setDefaults(newSurvey);
        return newSurvey;
    }
    
    public static Survey createSurveyWithDefaultDate(
        Long id, String name
    )
    {
        Survey newSurvey = new Survey();
        newSurvey.setId(id);
        newSurvey.setName(name == null ? null : name.getBytes(StandardCharsets.UTF_8));
        setDefaults(newSurvey);
        return newSurvey;
    }
    
    public static Survey createSurveyWithUserAndDefaultDate(
        Long id, String name, Long userId
    )
    {
        Survey newSurvey = new Survey();
        newSurvey.setId(id);
        newSurvey.setName(name == null ? null : name.getBytes(StandardCharsets.UTF_8));
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