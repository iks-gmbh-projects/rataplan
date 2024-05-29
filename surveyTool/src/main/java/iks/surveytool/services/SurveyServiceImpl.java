package iks.surveytool.services;

import iks.surveytool.domain.AuthUser;
import iks.surveytool.dtos.CompleteSurveyDTO;
import iks.surveytool.dtos.SurveyOverviewDTO;
import iks.surveytool.entities.*;
import iks.surveytool.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.apache.logging.log4j.Level;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class SurveyServiceImpl implements SurveyService {
    private final SurveyRepository surveyRepository;
    private final QuestionGroupRepository questionGroupRepository;
    private final QuestionRepository questionRepository;
    private final CheckboxGroupRepository checkboxGroupRepository;
    private final CheckboxRepository checkboxRepository;
    private final SurveyResponseRepository surveyResponseRepository;
    private final ModelMapper modelMapper;
    private final AuthService authService;
    private final Random random = new Random();
    
    public ResponseEntity<SurveyOverviewDTO> processSurveyDTO(CompleteSurveyDTO surveyDTO) throws InvalidEntityException
    {
        Survey newSurvey = mapSurveyToEntity(surveyDTO);
        newSurvey.validate();
        generateIds(newSurvey);
        Survey savedSurvey = saveSurvey(newSurvey);
        SurveyOverviewDTO completeSurveyDTO = mapSurveyToDTO(savedSurvey);
        return ResponseEntity.ok(completeSurveyDTO);
    }
    
    public ResponseEntity<SurveyOverviewDTO> processSurveyByAccessId(String accessId, String jwttoken) {
        SurveyOverviewDTO surveyOverviewDTO = mapSurveyToDTOByAccessId(accessId);
        if(surveyOverviewDTO != null) {
            if(surveyOverviewDTO.getUserId() != null) {
                AuthUser user = authService.getUserData(jwttoken);
                if(user == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
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
        if(surveyDTO != null) {
            if(surveyDTO instanceof CompleteSurveyDTO) {
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
        ZonedDateTime currentDateTime = ZonedDateTime.now();
        return surveyRepository.findAllByOpenAccessIsTrueAndEndDateIsAfterOrderByStartDate(currentDateTime);
    }
    
    private SurveyOverviewDTO mapSurveyToDTO(Survey savedSurvey) {
        return modelMapper.map(savedSurvey, CompleteSurveyDTO.class);
    }
    
    private SurveyOverviewDTO mapSurveyToDTOByAccessId(String accessId) {
        Optional<Survey> surveyOptional = findSurveyByAccessId(accessId);
        if(surveyOptional.isPresent()) {
            Survey survey = surveyOptional.get();
            return modelMapper.map(survey, CompleteSurveyDTO.class);
        }
        return null;
    }
    
    private SurveyOverviewDTO mapSurveyToDTOByParticipationId(String participationId) {
        Optional<Survey> surveyOptional = findSurveyByParticipationId(participationId);
        if(surveyOptional.isPresent()) {
            Survey survey = surveyOptional.get();
            ZonedDateTime zonedStartDate = survey.getStartDate();
            ZonedDateTime zonedEndDate = survey.getEndDate();
            ZonedDateTime currentDateTime = ZonedDateTime.now();
            if(currentDateTime.isAfter(zonedStartDate) && currentDateTime.isBefore(zonedEndDate)) {
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
    
    private List<SurveyOverviewDTO> mapSurveysToDTO(List<Survey> surveys) {
        Type surveyOverviewList = new TypeToken<List<SurveyOverviewDTO>>() {}.getType();
        return modelMapper.map(surveys, surveyOverviewList);
    }
    
    private List<SurveyOverviewDTO> mapSurveysToDTOByOpenIsTrue() {
        List<Survey> openAccessSurveys = findSurveysByOpenAccessIsTrue();
        return mapSurveysToDTO(openAccessSurveys);
    }
    
    private Survey mapSurveyToEntity(CompleteSurveyDTO surveyDTO) {
        return modelMapper.map(surveyDTO, Survey.class);
    }
    
    private Survey saveSurvey(Survey survey) {
        return surveyRepository.save(survey);
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
    public ResponseEntity<SurveyOverviewDTO> processEditSurveyByAccessId(
        String accessId, CompleteSurveyDTO completeSurveyDTO, String jwttoken
    ) throws InvalidEntityException
    {
        final Optional<Survey> optionalSurvey = findSurveyByAccessId(accessId);
        if(optionalSurvey.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        final Survey oldSurvey = optionalSurvey.get();
        if(oldSurvey.getStartDate().toInstant().isAfter(completeSurveyDTO.getStartDate()) &&
           !oldSurvey.getStartDate().toInstant().isAfter(Instant.now()))
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
        Survey survey = mapSurveyToEntity(completeSurveyDTO);
        survey.setId(oldSurvey.getId());
        survey.setUserId(oldSurvey.getUserId());
        survey.setAccessId(oldSurvey.getAccessId());
        survey.setParticipationId(oldSurvey.getParticipationId());
        
        survey.validate();
        
        transferValues(survey, oldSurvey);
        
        survey = saveSurvey(oldSurvey);
        surveyResponseRepository.deleteAllBySurvey(survey);
        SurveyOverviewDTO surveyOverviewDTO = mapSurveyToDTO(survey);
        return ResponseEntity.ok(surveyOverviewDTO);
    }
    
    private void transferValues(Survey from, Survey to) {
        to.setName(from.getName());
        to.setDescription(from.getDescription());
        to.setStartDate(from.getStartDate());
        to.setEndDate(from.getEndDate());
        to.setAnonymousParticipation(from.isAnonymousParticipation());
        to.setOpenAccess(from.isOpenAccess());
        
        Map<Boolean, List<QuestionGroup>> fromGroups = from.getQuestionGroups()
            .stream()
            .collect(Collectors.partitioningBy(idExistsIn(to.getQuestionGroups())));
        Map<Long, QuestionGroup> fromMap = fromGroups.get(true)
            .stream()
            .collect(Collectors.toMap(QuestionGroup::getId, Function.identity()));
        
        ListIterator<QuestionGroup> it = to.getQuestionGroups().listIterator();
        while(it.hasNext()) {
            QuestionGroup toQuestionGroup = it.next();
            QuestionGroup fromQuestionGroup = fromMap.get(toQuestionGroup.getId());
            if(fromQuestionGroup == null) {
                it.remove();
                questionGroupRepository.delete(toQuestionGroup);
                continue;
            }
            transferValues(fromQuestionGroup, toQuestionGroup);
        }
        to.getQuestionGroups().addAll(fromGroups.get(false));
        fromGroups.get(false).forEach(qg -> qg.setSurvey(to));
    }
    
    private void transferValues(QuestionGroup from, QuestionGroup to) {
        to.setTitle(from.getTitle());
        
        Map<Boolean, List<Question>> fromQuestions = from.getQuestions()
            .stream()
            .collect(Collectors.partitioningBy(idExistsIn(to.getQuestions())));
        Map<Long, Question> fromMap = fromQuestions.get(true)
            .stream()
            .collect(Collectors.toMap(Question::getId, Function.identity()));
        
        ListIterator<Question> it = to.getQuestions().listIterator();
        while(it.hasNext()) {
            Question toQuestion = it.next();
            Question fromQuestion = fromMap.get(toQuestion.getId());
            if(fromQuestion == null) {
                it.remove();
                questionRepository.delete(toQuestion);
                continue;
            }
            transferValues(fromQuestion, toQuestion);
        }
        to.getQuestions().addAll(fromQuestions.get(false));
        fromQuestions.get(false).forEach(q -> q.setQuestionGroup(to));
    }
    
    private void transferValues(Question from, Question to) {
        to.setText(from.getText());
        to.setRequired(from.isRequired());
        to.setHasCheckbox(from.isHasCheckbox());
        
        if(to.getCheckboxGroup() != null && from.getCheckboxGroup() != null) {
            transferValues(from.getCheckboxGroup(), to.getCheckboxGroup());
        } else {
            if(from.getCheckboxGroup() != null) from.getCheckboxGroup().setQuestion(to);
            if(to.getCheckboxGroup() != null) checkboxGroupRepository.delete(to.getCheckboxGroup());
            to.setCheckboxGroup(from.getCheckboxGroup());
        }
    }
    
    private void transferValues(CheckboxGroup from, CheckboxGroup to) {
        to.setMultipleSelect(from.isMultipleSelect());
        to.setMinSelect(from.getMinSelect());
        to.setMaxSelect(from.getMaxSelect());
        
        Map<Boolean, List<Checkbox>> fromQuestions = from.getCheckboxes()
            .stream()
            .collect(Collectors.partitioningBy(idExistsIn(to.getCheckboxes())));
        Map<Long, Checkbox> fromMap = fromQuestions.get(true)
            .stream()
            .collect(Collectors.toMap(Checkbox::getId, Function.identity()));
        
        ListIterator<Checkbox> it = to.getCheckboxes().listIterator();
        while(it.hasNext()) {
            Checkbox toCheckbox = it.next();
            Checkbox fromCheckbox = fromMap.get(toCheckbox.getId());
            if(fromCheckbox == null) {
                it.remove();
                checkboxRepository.delete(toCheckbox);
                continue;
            }
            toCheckbox.setText(fromCheckbox.getText());
            toCheckbox.setHasTextField(fromCheckbox.isHasTextField());
        }
        fromQuestions.get(false).forEach(c -> {
            c.setCheckboxGroup(to);
            c.setId(null);
        });
        to.getCheckboxes().addAll(fromQuestions.get(false));
    }
    
    private static <T extends AbstractEntity> Predicate<T> idExistsIn(Collection<T> collection) {
        return e -> e.getId() != null && collection.stream().map(T::getId).anyMatch(e.getId()::equals);
    }
    
    @Override
    public ResponseEntity<List<SurveyOverviewDTO>> processMySurveys(String jwttoken) {
        AuthUser user = authService.getUserData(jwttoken);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Survey> surveys = surveyRepository.findAllByUserId(user.getId());
        List<SurveyOverviewDTO> surveyDTOs = mapSurveysToDTO(surveys);
        return ResponseEntity.ok(surveyDTOs);
    }
    
    @Override
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
