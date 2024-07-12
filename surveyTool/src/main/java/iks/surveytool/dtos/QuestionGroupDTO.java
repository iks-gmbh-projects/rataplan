package iks.surveytool.dtos;

import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class QuestionGroupDTO extends AbstractDTO {

    private String title;

    private List<QuestionDTO> questions;

    @Builder
    public QuestionGroupDTO(Long id, String title, @Singular List<QuestionDTO> questions) {
        super(id);
        this.title = title;
        this.questions = questions;
    }
    
    @Override
    public void resetId() {
        super.resetId();
        questions.forEach(AbstractDTO::resetId);
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