package iks.surveytool.dtos;

import lombok.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class SurveyResponseDTO extends AbstractDTO {
    private long surveyId;
    private Long userId;
    private Map<Long, Map<Integer, AnswerDTO>> answers = new HashMap<>();
    
    @Override
    public void resetId() {
    }
    
    @Override
    public void trimAndNull() {
        if(answers != null) answers.values()
            .stream()
            .map(Map::values)
            .flatMap(Collection::stream)
            .forEach(AnswerDTO::trimAndNull);
    }
    
    @Override
    public void valid() throws DTOValidationException {
        if(answers == null) throw new DTOValidationException("SurveyResponse.answers", "is null");
        for(
            Iterator<AnswerDTO> it = answers.values().stream().map(Map::values).flatMap(Collection::stream).iterator();
            it.hasNext(); ) {
            it.next().valid();
        }
    }
}