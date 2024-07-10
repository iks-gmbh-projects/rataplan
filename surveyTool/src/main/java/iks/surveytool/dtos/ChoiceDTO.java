package iks.surveytool.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChoiceDTO extends AbstractDTO {

    private String text;
    private boolean hasTextField;

    public ChoiceDTO(Long id, String text, boolean hasTextField) {
        super(id);
        this.text = text;
        this.hasTextField = hasTextField;
    }
    
    @Override
    public void trimAndNull() {
        text = trimAndNull(text);
    }
    
    @Override
    public void valid() throws DTOValidationException {
        if(this.text != null && this.text.isBlank()) throw new DTOValidationException("CheckboxDTO.text", "blank non-null");
    }
}