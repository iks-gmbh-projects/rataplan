package iks.surveytool.dtos;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class ChoiceDTO extends AbstractDTO {

    private String text;
    private boolean hasTextField;

    @Builder
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