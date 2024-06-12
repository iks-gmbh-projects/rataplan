package iks.surveytool.services;

import iks.surveytool.domain.AuthUser;
import iks.surveytool.dtos.*;
import iks.surveytool.entities.*;
import iks.surveytool.repositories.SurveyRepository;
import iks.surveytool.repositories.SurveyResponseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.apache.logging.log4j.Level;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class SurveyServiceImpl implements SurveyService {
    private final SurveyRepository surveyRepository;
    private final SurveyResponseRepository surveyResponseRepository;
    private final ModelMapper mapper;
    private final AuthService authService;
    private final Random random = new Random();
    
    @Transactional
    public ResponseEntity<SurveyOverviewDTO> createSurvey(CompleteSurveyDTO surveyDTO, Jwt jwtToken) throws
        InvalidEntityException
    {
        final AuthUser user = authService.getUserData(jwtToken);
        final Long userId = user == null ? null : user.getId();
        surveyDTO.setId(null);
        surveyDTO.setUserId(userId);
        Survey survey = mapper.map(surveyDTO, Survey.class);
        generateIds(survey);
        survey.bindChildren();
        survey.validate();
        Survey savedSurvey = surveyRepository.save(survey);
        SurveyOverviewDTO completeSurveyDTO = mapper.map(savedSurvey, CompleteSurveyDTO.class);
        return ResponseEntity.ok(completeSurveyDTO);
    }
    
    @Transactional(readOnly = true)
    public ResponseEntity<? extends SurveyOverviewDTO> getSurveyByAccessId(String accessId, Jwt jwttoken) {
        return surveyRepository.findSurveyByAccessId(accessId)
            .map(survey -> mapper.map(survey, CompleteSurveyDTO.class))
            .map(surveyOverviewDTO -> {
                if(surveyOverviewDTO.getUserId() != null) {
                    AuthUser user = authService.getUserData(jwttoken);
                    if(user == null) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).<SurveyOverviewDTO>build();
                    }
                    if(!Objects.equals(surveyOverviewDTO.getUserId(), user.getId())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<SurveyOverviewDTO>build();
                    }
                }
                return ResponseEntity.ok(surveyOverviewDTO);
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    
    @Transactional(readOnly = true)
    public ResponseEntity<? extends CompleteSurveyDTO> getSurveyByParticipationId(String participationId) {
        return surveyRepository.findSurveyByParticipationId(participationId)
            .filter(survey -> survey.isActiveAt(Instant.now()))
            .map(survey -> mapper.map(survey, CompleteSurveyDTO.class))
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    
    @Transactional(readOnly = true)
    public ResponseEntity<List<SurveyOverviewDTO>> getCurrentOpenSurveys() {
        return ResponseEntity.ok(mapper.map(
            surveyRepository.findAllByOpenAccessIsTrueAndEndDateIsAfterOrderByStartDate(ZonedDateTime.now()),
            new TypeToken<List<SurveyOverviewDTO>>() {}.getType()
        ));
    }
    
    private void generateIds(Survey survey) {
        ZonedDateTime currentDateTime = ZonedDateTime.now();
        String accessId = generateAccessId(currentDateTime);
        survey.setAccessId(accessId);
        
        ZonedDateTime startDateTime = survey.getStartDate();
        String participateId = generateParticipationId(startDateTime);
        survey.setParticipationId(participateId);
    }
    
    private String generateAccessId(ZonedDateTime currentDateTime) {
        String currentDateTimeHex = convertDateTimeToHex(currentDateTime);
        String hexSuffix = generateHexSuffix();
        String accessId = currentDateTimeHex + "-" + hexSuffix;
        
        while(surveyRepository.findSurveyByAccessId(accessId).isPresent()) {
            hexSuffix = generateHexSuffix();
            accessId = currentDateTimeHex + "-" + hexSuffix;
        }
        
        return accessId.toUpperCase();
    }
    
    private String generateParticipationId(ZonedDateTime startDate) {
        String currentDateTimeHex = convertDateTimeToHex(startDate);
        String hexSuffix = generateHexSuffix();
        String participationId = currentDateTimeHex + "-" + hexSuffix;
        
        while(surveyRepository.findSurveyByParticipationId(participationId).isPresent()) {
            hexSuffix = generateHexSuffix();
            participationId = currentDateTimeHex + "-" + hexSuffix;
        }
        
        return participationId.toUpperCase();
    }
    
    private String convertDateTimeToHex(ZonedDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mmkddMMyy");
        String formattedDate = dateTime.format(formatter);
        long formattedDateLong = Long.parseLong(formattedDate);
        
        return Long.toHexString(formattedDateLong);
    }
    
    private String generateHexSuffix() {
        int randomNumber = random.nextInt(256);
        return Integer.toHexString(randomNumber);
    }
    
    @Override
    @Transactional
    public ResponseEntity<SurveyOverviewDTO> editSurvey(
        String accessId,
        CompleteSurveyDTO completeSurveyDTO,
        Jwt jwttoken
    ) throws InvalidEntityException
    {
        final Optional<Survey> optionalSurvey = surveyRepository.findSurveyByAccessId(accessId);
        if(optionalSurvey.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        final Survey oldSurvey = optionalSurvey.get();
        completeSurveyDTO.setId(oldSurvey.getId());
        if(oldSurvey.getStartDate().toInstant().isAfter(completeSurveyDTO.getStartDate()) &&
           Instant.now().isAfter(completeSurveyDTO.getStartDate()))
            throw new InvalidEntityException("Invalid Start Date", oldSurvey);
        if(!completeSurveyDTO.getEndDate().isAfter(completeSurveyDTO.getStartDate()))
            throw new InvalidEntityException("Invalid End Date", oldSurvey);
        if(oldSurvey.getUserId() != null) {
            AuthUser user = authService.getUserData(jwttoken);
            if(user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            if(oldSurvey.getUserId().longValue() != user.getId().longValue()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        completeSurveyDTO.setUserId(oldSurvey.getUserId());
        completeSurveyDTO.setAccessId(oldSurvey.getAccessId());
        completeSurveyDTO.setParticipationId(oldSurvey.getParticipationId());
        mapper.map(completeSurveyDTO, oldSurvey);
        oldSurvey.bindChildren();
        oldSurvey.validate();
        
        Survey survey = surveyRepository.saveAndFlush(oldSurvey);
        surveyResponseRepository.deleteAllBySurvey(survey);
        surveyResponseRepository.flush();
        SurveyOverviewDTO surveyOverviewDTO = mapper.map(survey, CompleteSurveyDTO.class);
        return ResponseEntity.ok(surveyOverviewDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<SurveyOverviewDTO>> getUserSurveys(Jwt jwttoken) {
        AuthUser user = authService.getUserData(jwttoken);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(mapper.map(surveyRepository.findAllByUserId(user.getId()), new TypeToken<List<SurveyOverviewDTO>>() {}.getType()));
    }
    
    @Override
    @Transactional
    public ResponseEntity<?> deleteSurveysByUserId(long id) {
        try {
            surveyRepository.deleteSurveysByUserId(id);
            return ResponseEntity.accepted().body(id);
        } catch(DataAccessException ex) {
            log.catching(Level.INFO, ex);
            return ResponseEntity.internalServerError().body(ex.getMostSpecificCause().getMessage());
        }
    }
    
    @Override
    @Transactional
    public ResponseEntity<?> anonymizeSurveysByUserId(long id) {
        try {
            final List<Survey> surveys = surveyRepository.findAllByUserId(id);
            surveys.forEach(survey -> survey.setUserId(null));
            surveyRepository.saveAllAndFlush(surveys);
            return ResponseEntity.accepted().body(id);
        } catch(DataAccessException ex) {
            log.catching(Level.INFO, ex);
            return ResponseEntity.internalServerError().body(ex.getMostSpecificCause().getMessage());
        }
    }
}