package iks.surveytool.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class QuestionGroupDTO extends AbstractDTO {

    private String title;

    private List<QuestionDTO> questions;

    public QuestionGroupDTO(Long id, String title, List<QuestionDTO> questions) {
        super(id);
        this.title = title;
        this.questions = questions;
    }
    
    @Override
    public void trimAndNull() {
        title = trimAndNull(title);
        if(questions != null) questions.forEach(QuestionDTO::trimAndNull);
    }
    
    @Override
    public void valid() throws DTOValidationException {
        if(this.title != null && this.title.isBlank()) throw new DTOValidationException("QuestionGroupDTO.title", "blank non-null");
        for(QuestionDTO q : questions) {
            q.valid();
        }
    }
}