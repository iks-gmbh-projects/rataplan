package iks.surveytool.dtos;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerDTO {
    private String text;
    private Map<Long, Boolean> checkboxes;
    private List<Long> order;

    public AnswerDTO(String text) {
        this.text = text;
    }
    
    public AnswerDTO(Map<Long, Boolean> checkboxes) {
        this.checkboxes = checkboxes;
    }
    
    public AnswerDTO(List<Long> order) {
        this.order = order;
    }
    
    public void trimAndNull() {
        text = AbstractDTO.trimAndNull(text);
    }
    
    public void valid() throws DTOValidationException {
        if(this.text != null && this.text.isBlank()) throw new DTOValidationException("AnswerDTO.text", "blank non-null");
    }
}