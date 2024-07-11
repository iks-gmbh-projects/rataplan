package iks.surveytool.dtos;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class SurveyResponseDTO extends AbstractDTO {
    private long surveyId;
    private Long userId;
    private Map<Long, AnswerDTO> answers = new HashMap<>();
    
    @Override
    public void trimAndNull() {
        if(answers != null) answers.values().forEach(AnswerDTO::trimAndNull);
    }
    
    @Override
    public void valid() throws DTOValidationException {
        if(answers == null) throw new DTOValidationException("SurveyResponse.answers", "is null");
        for(AnswerDTO a : answers.values()) {
            a.valid();
        }
    }
}