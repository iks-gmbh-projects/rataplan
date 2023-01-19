package iks.surveytool.mapping;

import iks.surveytool.dtos.*;
import iks.surveytool.entities.*;
import iks.surveytool.mapping.crypto.ToEncryptedStringConverter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SurveyConverter extends AbstractConverter<CompleteSurveyDTO, Survey> {
    private final ToEncryptedStringConverter toEncryptedStringConverter;
    
    @Override
    protected Survey convert(CompleteSurveyDTO source) {
        return toSurveyEntity(source);
    }
    
    private static ZonedDateTime toLocalZoneDateTime(Instant instant) {
        if (instant == null) return null;
        return instant.atZone(ZoneId.systemDefault());
    }
    
    public Survey toSurveyEntity(CompleteSurveyDTO surveyDTO) {
        List<QuestionGroup> questionGroups = toQuestionGroupEntityList(surveyDTO.getQuestionGroups());
        
        Survey newSurvey = new Survey(
            toEncryptedStringConverter.convert(surveyDTO.getName()),
            toEncryptedStringConverter.convert(surveyDTO.getDescription()),
            toLocalZoneDateTime(surveyDTO.getStartDate()),
            toLocalZoneDateTime(surveyDTO.getEndDate()),
            surveyDTO.isOpenAccess(),
            surveyDTO.isAnonymousParticipation(),
            surveyDTO.getAccessId(),
            surveyDTO.getParticipationId(),
            questionGroups
        );
        for(QuestionGroup qg: newSurvey.getQuestionGroups()) {
            qg.setSurvey(newSurvey);
        }
        
        newSurvey.setId(surveyDTO.getId());
        newSurvey.setUserId(surveyDTO.getUserId());
        
        return newSurvey;
    }
    
    private List<QuestionGroup> toQuestionGroupEntityList(List<QuestionGroupDTO> questionGroupDTOs) {
        return questionGroupDTOs.stream().map(this::toQuestionGroupEntity).collect(Collectors.toList());
    }
    
    private QuestionGroup toQuestionGroupEntity(QuestionGroupDTO questionGroupDTO) {
        QuestionGroup questionGroup = new QuestionGroup(
            toEncryptedStringConverter.convert(questionGroupDTO.getTitle()),
            toQuestionEntityList(questionGroupDTO.getQuestions())
        );
        questionGroup.setId(questionGroupDTO.getId());
        for(Question q:questionGroup.getQuestions()) {
            q.setQuestionGroup(questionGroup);
        }
        return questionGroup;
    }
    
    private List<Question> toQuestionEntityList(List<QuestionDTO> questionDTOs) {
        return questionDTOs.stream().map(this::toQuestionEntity).collect(Collectors.toList());
    }
    
    private Question toQuestionEntity(QuestionDTO questionDTO) {
        Question question = new Question(toEncryptedStringConverter.convert(questionDTO.getText()), questionDTO.isRequired(), questionDTO.isHasCheckbox());
        question.setId(questionDTO.getId());
        if (questionDTO.isHasCheckbox()) {
            CheckboxGroup checkboxGroup = toCheckboxGroupEntity(questionDTO.getCheckboxGroup());
            // Set references for the one-to-one-relationship
            checkboxGroup.setQuestion(question);
            question.setCheckboxGroup(checkboxGroup);
        }
        
        return question;
    }
    
    private CheckboxGroup toCheckboxGroupEntity(CheckboxGroupDTO checkboxGroupDTO) {
        CheckboxGroup checkboxGroup = new CheckboxGroup(checkboxGroupDTO.isMultipleSelect(),
            checkboxGroupDTO.getMinSelect(),
            checkboxGroupDTO.getMaxSelect(),
            toCheckboxEntityList(checkboxGroupDTO.getCheckboxes())
        );
        checkboxGroup.setId(checkboxGroupDTO.getId());
        for(Checkbox cb:checkboxGroup.getCheckboxes()) {
            cb.setCheckboxGroup(checkboxGroup);
        }
        return checkboxGroup;
    }
    
    private List<Checkbox> toCheckboxEntityList(List<CheckboxDTO> checkboxDTOs) {
        return checkboxDTOs.stream().map(this::toCheckboxEntity).collect(Collectors.toList());
    }
    
    private Checkbox toCheckboxEntity(CheckboxDTO checkboxDTO) {
        Checkbox checkbox = new Checkbox(toEncryptedStringConverter.convert(checkboxDTO.getText()), checkboxDTO.isHasTextField());
        checkbox.setId(checkboxDTO.getId());
        return checkbox;
    }
    
}
