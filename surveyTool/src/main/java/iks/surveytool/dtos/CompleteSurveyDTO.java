package iks.surveytool.dtos;


import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CompleteSurveyDTO extends SurveyOverviewDTO {
    private List<QuestionGroupDTO> questionGroups;
    
    @Override
    public void trimAndNull() {
        super.trimAndNull();
        if(questionGroups != null) questionGroups.forEach(QuestionGroupDTO::trimAndNull);
    }
    
    @Override
    public void valid() throws DTOValidationException {
        super.valid();
        for(QuestionGroupDTO g : questionGroups) {
            g.valid();
        }
    }
}