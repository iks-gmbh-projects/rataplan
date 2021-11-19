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

    public Optional<Survey> findSurveyByParticipationID(String participationID) {
        return surveyRepository.findSurveyByParticipationID(participationID);
    }

    public Optional<Survey> findSurveyByAccessId(String accessId) {
        return surveyRepository.findSurveyByAccessID(accessId);
    }

    public List<Survey> findSurveysByOpenIsTrue() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return surveyRepository.findSurveysByOpenIsTrueAndEndDateIsAfterOrderByStartDate(currentDateTime);
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

    public SurveyOverviewDTO createSurveyDtoByParticipationId(String participationID) {
        Optional<Survey> surveyOptional = findSurveyByParticipationID(participationID);
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
        List<Survey> openAccessSurveys = findSurveysByOpenIsTrue();
        return mapper.toOpenAccessSurveyDtoList(openAccessSurveys);

    }

    public Survey createSurveyFromDto(CompleteSurveyDTO surveyDTO) {
        return mapper.createSurveyFromDto(surveyDTO);
    }

    public Survey saveSurvey(Survey survey) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        String accessID = generateUniqueIdWithDateTime(currentDateTime);
        survey.setAccessID(accessID);

        LocalDateTime startDateTime = survey.getStartDate();
        String participateID = generateUniqueIdWithDateTime(startDateTime);
        survey.setParticipationID(participateID);
        return surveyRepository.save(survey);
    }

    public String generateUniqueIdWithDateTime(LocalDateTime currentDateTime) {
        String currentDateTimeHex = convertDateTimeToHex(currentDateTime);

        String hexSuffix = generateHexSuffix();

        String accessID = currentDateTimeHex + "-" + hexSuffix;

        while (surveyRepository.findSurveyByAccessID(accessID).isPresent()) {
            hexSuffix = generateHexSuffix();
            accessID = currentDateTimeHex + "-" + hexSuffix;
        }

        return accessID.toUpperCase();
    }

    private String convertDateTimeToHex(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mmhhddMM");
        String formattedDate = dateTime.format(formatter);
        int formattedDateInteger = Integer.parseInt(formattedDate);

        return Integer.toHexString(formattedDateInteger);
    }

    private String generateHexSuffix() {
        Random random = new Random();
        int randomNumber = random.nextInt(256);
        return Integer.toHexString(randomNumber);
    }
}
