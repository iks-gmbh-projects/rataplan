package iks.surveytool.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class SurveyResponseDTO extends AbstractDTO {
    private long surveyId;
    private Long userId;
    private Map<Long, AnswerDTO> answers;
    
    @Override
    public void trimAndNull() {
        if(answers != null) answers.values().forEach(AnswerDTO::trimAndNull);
    }
    
    @Override
    public boolean valid() {
        return answers != null && answers.values().stream().allMatch(AnswerDTO::valid);
    }
}
