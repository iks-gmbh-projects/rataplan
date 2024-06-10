package iks.surveytool.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuestionDTO extends AbstractDTO {

    private String text;
    private boolean required;
    private boolean hasCheckbox;

    private CheckboxGroupDTO checkboxGroup;

    public QuestionDTO(Long id, String text, boolean required, boolean hasCheckbox) {
        super(id);
        this.text = text;
        this.required = required;
        this.hasCheckbox = hasCheckbox;
    }

    public QuestionDTO(Long id, String text, boolean required, boolean hasCheckbox, CheckboxGroupDTO checkboxGroup) {
        this(id, text, required, hasCheckbox);
        this.checkboxGroup = checkboxGroup;
    }
    
    @Override
    public void trimAndNull() {
        text = trimAndNull(text);
        if(checkboxGroup != null) checkboxGroup.trimAndNull();
    }
    
    @Override
    public void valid() throws DTOValidationException {
        if(this.text != null && this.text.isBlank()) throw new DTOValidationException("QuestionDTO.text", "blank non-null");
        if(hasCheckbox == (checkboxGroup == null)) throw new DTOValidationException("QuestionDTO.hasCheckbox", "state-mismatch");
        if(checkboxGroup != null) checkboxGroup.valid();
    }
}