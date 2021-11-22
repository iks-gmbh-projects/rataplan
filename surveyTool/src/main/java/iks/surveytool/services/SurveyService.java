package iks.surveytool.services;

import iks.surveytool.components.Mapper;
import iks.surveytool.dtos.CompleteSurveyDTO;
import iks.surveytool.dtos.SurveyOverviewDTO;
import iks.surveytool.entities.Survey;
import iks.surveytool.repositories.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class SurveyService {
    private final SurveyRepository surveyRepository;
    private final Mapper mapper;

    public Optional<Survey> findSurveyByParticipationId(String participationId) {
        return surveyRepository.findSurveyByParticipationId(participationId);
    }

    public Optional<Survey> findSurveyByAccessId(String accessId) {
        return surveyRepository.findSurveyByAccessId(accessId);
    }

    public List<Survey> findSurveysByOpenAccessIsTrue() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return surveyRepository.findSurveysByOpenAccessIsTrueAndEndDateIsAfterOrderByStartDate(currentDateTime);
    }

    public SurveyOverviewDTO createSurveyDtoFromSurvey(Survey savedSurvey) {
        return mapper.toSurveyDto(savedSurvey, true);
    }

    public SurveyOverviewDTO createSurveyDtoByAccessId(String accessId, boolean complete) {
        Optional<Survey> surveyOptional = findSurveyByAccessId(accessId);
        if (surveyOptional.isPresent()) {
            Survey survey = surveyOptional.get();
            return mapper.toSurveyDto(survey, complete);
        }
        return null;
    }

    public SurveyOverviewDTO createSurveyDtoByParticipationId(String participationId) {
        Optional<Survey> surveyOptional = findSurveyByParticipationId(participationId);
        if (surveyOptional.isPresent()) {
            Survey survey = surveyOptional.get();
            LocalDateTime surveyStartDate = survey.getStartDate();
            LocalDateTime surveyEndDate = survey.getEndDate();
            LocalDateTime currentDateTime = LocalDateTime.now();
            if (currentDateTime.isAfter(surveyStartDate) && currentDateTime.isBefore(surveyEndDate)) {
                return mapper.toSurveyDto(survey, true);
            } else {
                // If current time is not within start- and endDate: return survey without questions to fill information
                // in front-end
                return mapper.toSurveyDto(survey, false);
            }
        } else {
            return null;
        }
    }

    public List<SurveyOverviewDTO> createSurveyDtosByOpenIsTrue() {
        List<Survey> openAccessSurveys = findSurveysByOpenAccessIsTrue();
        return mapper.toOpenAccessSurveyDtoList(openAccessSurveys);

    }

    public Survey createSurveyFromDto(CompleteSurveyDTO surveyDTO) {
        return mapper.createSurveyFromDto(surveyDTO);
    }

    public Survey saveSurvey(Survey survey) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        String accessId = generateUniqueIdWithDateTime(currentDateTime);
        survey.setAccessId(accessId);

        LocalDateTime startDateTime = survey.getStartDate();
        String participateId = generateUniqueIdWithDateTime(startDateTime);
        survey.setParticipationId(participateId);
        return surveyRepository.save(survey);
    }

    public String generateUniqueIdWithDateTime(LocalDateTime currentDateTime) {
        String currentDateTimeHex = convertDateTimeToHex(currentDateTime);

        String hexSuffix = generateHexSuffix();

        String accessId = currentDateTimeHex + "-" + hexSuffix;

        while (surveyRepository.findSurveyByAccessId(accessId).isPresent()) {
            hexSuffix = generateHexSuffix();
            accessId = currentDateTimeHex + "-" + hexSuffix;
        }

        return accessId.toUpperCase();
    }

    private String convertDateTimeToHex(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mmkddMMyy");
        String formattedDate = dateTime.format(formatter);
        long formattedDateLong = Long.parseLong(formattedDate);

        return Long.toHexString(formattedDateLong);
    }

    private String generateHexSuffix() {
        Random random = new Random();
        int randomNumber = random.nextInt(256);
        return Integer.toHexString(randomNumber);
    }
}
