package iks.surveytool.services;

import iks.surveytool.domain.AuthUser;
import iks.surveytool.dtos.SurveyResponseDTO;
import iks.surveytool.entities.InvalidEntityException;
import iks.surveytool.entities.Survey;
import iks.surveytool.entities.SurveyResponse;
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

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class SurveyResponseServiceImpl implements SurveyResponseService {
    private final SurveyResponseRepository surveyResponseRepository;
    private final SurveyRepository surveyRepository;
    private final AuthService authService;
    private final ModelMapper modelMapper;
    
    @Override
    @Transactional
    public ResponseEntity<SurveyResponseDTO> acceptSurveyResponse(SurveyResponseDTO surveyResponseDTO) throws
        InvalidEntityException
    {
        SurveyResponse surveyResponse = modelMapper.map(surveyResponseDTO, SurveyResponse.class);
        surveyResponse.validate();
        if(!validateUniqueParticipation(surveyResponse)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        SurveyResponse savedAnswers = surveyResponseRepository.save(surveyResponse);
        SurveyResponseDTO savedAnswerDTOs = modelMapper.map(savedAnswers, SurveyResponseDTO.class);
        return ResponseEntity.ok(savedAnswerDTOs);
    }
    
    private boolean validateUniqueParticipation(SurveyResponse surveyResponse) {
        if(surveyResponse.getUserId() != null) {
            return !surveyResponseRepository.existsBySurveyIdAndAndUserId(surveyResponse.getSurvey().getId(),
                surveyResponse.getUserId()
            );
        }
        return true;
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<SurveyResponseDTO>> getSurveyResponses(String accessId, Jwt authToken) {
        final Optional<Survey> optSurvey = surveyRepository.findSurveyByAccessId(accessId);
        if(optSurvey.isEmpty()) return ResponseEntity.notFound().build();
        final Survey survey = optSurvey.get();
        if(survey.getUserId() != null) {
            final AuthUser user = authService.getUserData(authToken);
            if(user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            if(survey.getUserId().longValue() != user.getId().longValue())
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(modelMapper.map(
            surveyResponseRepository.findAllBySurvey(survey),
            new TypeToken<List<SurveyResponseDTO>>() {}.getType()
        ));
    }
    
    @Override
    @Transactional
    public ResponseEntity<?> deleteSurveyResponsesByUserId(long id) {
        try {
            surveyResponseRepository.deleteAllByUserId(id);
            return ResponseEntity.accepted().body(id);
        } catch(DataAccessException ex) {
            log.catching(Level.INFO, ex);
            return ResponseEntity.internalServerError().body(ex.getMostSpecificCause().getMessage());
        }
    }
    
    @Override
    @Transactional
    public ResponseEntity<?> anonymizeSurveyResponsesByUserId(long id) {
        try {
            final List<SurveyResponse> surveyResponses = surveyResponseRepository.findAllByUserId(id);
            surveyResponses.forEach(surveyResponse -> surveyResponse.setUserId(null));
            surveyResponseRepository.saveAllAndFlush(surveyResponses);
            return ResponseEntity.accepted().body(id);
        } catch(DataAccessException ex) {
            log.catching(Level.INFO, ex);
            return ResponseEntity.internalServerError().body(ex.getMostSpecificCause().getMessage());
        }
    }
}