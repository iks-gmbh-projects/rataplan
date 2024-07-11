package iks.surveytool.dtos;

import lombok.*;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDTO extends AbstractDTO {

    private String text;
    private Map<Long, Boolean> checkboxes;
    
    public AnswerDTO(Long id) {
        super(id);
    }

    public AnswerDTO(Long id, String text) {
        this(id);
        this.text = text;
    }
    
    public AnswerDTO(Long id, Map<Long, Boolean> checkboxes) {
        this(id);
        this.checkboxes = checkboxes;
    }
    
    @Override
    public void trimAndNull() {
        text = trimAndNull(text);
    }
    
    @Override
    public void valid() throws DTOValidationException {
        if(this.text != null && this.text.isBlank()) throw new DTOValidationException("AnswerDTO.text", "blank non-null");
    }
}