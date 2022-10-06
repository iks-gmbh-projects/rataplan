package iks.surveytool.services;

import iks.surveytool.domain.AuthUser;
import iks.surveytool.dtos.CompleteSurveyDTO;
import iks.surveytool.dtos.SurveyOverviewDTO;
import iks.surveytool.entities.Survey;
import iks.surveytool.repositories.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class SurveyServiceImpl implements SurveyService {
    private final SurveyRepository surveyRepository;
    private final ModelMapper modelMapper;
    private final AuthService authService;
    private final Random random = new Random();

    public ResponseEntity<SurveyOverviewDTO> processSurveyDTO(CompleteSurveyDTO surveyDTO) {
        Survey newSurvey = mapSurveyToEntity(surveyDTO);
        if (newSurvey.validate()) {
            generateIds(newSurvey);
            Survey savedSurvey = saveSurvey(newSurvey);
            SurveyOverviewDTO completeSurveyDTO = mapSurveyToDTO(savedSurvey);
            return ResponseEntity.ok(completeSurveyDTO);
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
    }

    public ResponseEntity<SurveyOverviewDTO> processSurveyByAccessId(String accessId, String jwttoken) {
        SurveyOverviewDTO surveyOverviewDTO = mapSurveyToDTOByAccessId(accessId);
        if (surveyOverviewDTO != null) {
            if(surveyOverviewDTO.getUserId() != null) {
                ResponseEntity<AuthUser> userEntity = authService.getUserData(jwttoken);
                if(!userEntity.getStatusCode().is2xxSuccessful()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
                AuthUser user = userEntity.getBody();
                if(user == null) return ResponseEntity.internalServerError().build();
                if(surveyOverviewDTO.getUserId().longValue() != user.getId().longValue()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
            return ResponseEntity.ok(surveyOverviewDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    public ResponseEntity<SurveyOverviewDTO> processSurveyByParticipationId(String participationId) {
        SurveyOverviewDTO surveyDTO = mapSurveyToDTOByParticipationId(participationId);
        if (surveyDTO != null) {
            if (surveyDTO instanceof CompleteSurveyDTO) {
                return ResponseEntity.ok(surveyDTO);
            } else {
                // If current time is not within start- and endDate: return survey without questions to fill information
                // in front-end
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(surveyDTO);
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    public ResponseEntity<List<SurveyOverviewDTO>> processOpenAccessSurveys() {
        List<SurveyOverviewDTO> openAccessSurveys = mapSurveysToDTOByOpenIsTrue();
        return ResponseEntity.ok(openAccessSurveys);
    }

    private Optional<Survey> findSurveyByParticipationId(String participationId) {
        return surveyRepository.findSurveyByParticipationId(participationId);
    }

    private Optional<Survey> findSurveyByAccessId(String accessId) {
        return surveyRepository.findSurveyByAccessId(accessId);
    }

    private List<Survey> findSurveysByOpenAccessIsTrue() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return surveyRepository.findSurveysByOpenAccessIsTrueAndEndDateIsAfterOrderByStartDate(currentDateTime);
    }

    private SurveyOverviewDTO mapSurveyToDTO(Survey savedSurvey) {
        return modelMapper.map(savedSurvey, CompleteSurveyDTO.class);
    }

    private SurveyOverviewDTO mapSurveyToDTOByAccessId(String accessId) {
        Optional<Survey> surveyOptional = findSurveyByAccessId(accessId);
        if (surveyOptional.isPresent()) {
            Survey survey = surveyOptional.get();
            return modelMapper.map(survey, CompleteSurveyDTO.class);
        }
        return null;
    }

    private SurveyOverviewDTO mapSurveyToDTOByParticipationId(String participationId) {
        Optional<Survey> surveyOptional = findSurveyByParticipationId(participationId);
        if (surveyOptional.isPresent()) {
            Survey survey = surveyOptional.get();
            ZoneId berlinTime = ZoneId.of("Europe/Berlin");
            LocalDateTime surveyStartDate = survey.getStartDate();
            ZonedDateTime zonedStartDate = ZonedDateTime.of(surveyStartDate, berlinTime);
            LocalDateTime surveyEndDate = survey.getEndDate();
            ZonedDateTime zonedEndDate = ZonedDateTime.of(surveyEndDate, berlinTime);
            ZonedDateTime currentDateTime = ZonedDateTime.now(berlinTime);
            if (currentDateTime.isAfter(zonedStartDate) && currentDateTime.isBefore(zonedEndDate)) {
                return modelMapper.map(survey, CompleteSurveyDTO.class);
            } else {
                // If current time is not within start- and endDate: return survey without questions to fill information
                // in front-end
                return modelMapper.map(survey, SurveyOverviewDTO.class);
            }
        } else {
            return null;
        }
    }

    private List<SurveyOverviewDTO> mapSurveysToDTOByOpenIsTrue() {
        List<Survey> openAccessSurveys = findSurveysByOpenAccessIsTrue();
        Type surveyOverviewList = new TypeToken<List<SurveyOverviewDTO>>() {
        }.getType();
        return modelMapper.map(openAccessSurveys, surveyOverviewList);
    }

    private Survey mapSurveyToEntity(CompleteSurveyDTO surveyDTO) {
        return modelMapper.map(surveyDTO, Survey.class);
    }

    private Survey saveSurvey(Survey survey) {
        return surveyRepository.save(survey);
    }

    private void generateIds(Survey survey) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        String accessId = generateAccessId(currentDateTime);
        survey.setAccessId(accessId);

        LocalDateTime startDateTime = survey.getStartDate();
        String participateId = generateParticipationId(startDateTime);
        survey.setParticipationId(participateId);
    }

    private String generateAccessId(LocalDateTime currentDateTime) {
        String currentDateTimeHex = convertDateTimeToHex(currentDateTime);
        String hexSuffix = generateHexSuffix();
        String accessId = currentDateTimeHex + "-" + hexSuffix;

        while (surveyRepository.findSurveyByAccessId(accessId).isPresent()) {
            hexSuffix = generateHexSuffix();
            accessId = currentDateTimeHex + "-" + hexSuffix;
        }

        return accessId.toUpperCase();
    }

    private String generateParticipationId(LocalDateTime startDate) {
        String currentDateTimeHex = convertDateTimeToHex(startDate);
        String hexSuffix = generateHexSuffix();
        String participationId = currentDateTimeHex + "-" + hexSuffix;

        while (surveyRepository.findSurveyByParticipationId(participationId).isPresent()) {
            hexSuffix = generateHexSuffix();
            participationId = currentDateTimeHex + "-" + hexSuffix;
        }

        return participationId.toUpperCase();
    }

    private String convertDateTimeToHex(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mmkddMMyy");
        String formattedDate = dateTime.format(formatter);
        long formattedDateLong = Long.parseLong(formattedDate);

        return Long.toHexString(formattedDateLong);
    }

    private String generateHexSuffix() {
        int randomNumber = random.nextInt(256);
        return Integer.toHexString(randomNumber);
    }
}
