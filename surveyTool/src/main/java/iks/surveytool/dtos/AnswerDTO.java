package iks.surveytool.dtos;

import lombok.*;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDTO {
    private String text;
    private Map<Long, Boolean> checkboxes;

    public AnswerDTO(String text) {
        this.text = text;
    }
    
    public AnswerDTO(Map<Long, Boolean> checkboxes) {
        this.checkboxes = checkboxes;
    }
    
    public void trimAndNull() {
        text = AbstractDTO.trimAndNull(text);
    }
    
    public void valid() throws DTOValidationException {
        if(this.text != null && this.text.isBlank()) throw new DTOValidationException("AnswerDTO.text", "blank non-null");
    }
}