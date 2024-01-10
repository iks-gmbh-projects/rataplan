package iks.surveytool.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
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
    public boolean valid() {
        return super.valid() && questionGroups.stream().allMatch(QuestionGroupDTO::valid);
    }
}
