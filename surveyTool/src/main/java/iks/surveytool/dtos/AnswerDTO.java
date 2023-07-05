package iks.surveytool.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDTO extends AbstractDTO {

    private String text;
    private Map<Long, Boolean> checkboxes;

    public AnswerDTO(Long id, String text) {
        super(id);
        this.text = text;
    }
    
    @Override
    public void trimAndNull() {
        text = trimAndNull(text);
    }
    
    @Override
    public boolean valid() {
        return this.text == null || !this.text.isBlank();
    }
}
