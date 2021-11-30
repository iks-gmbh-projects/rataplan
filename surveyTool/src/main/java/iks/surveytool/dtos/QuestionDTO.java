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
        super(id);
        this.text = text;
        this.required = required;
        this.hasCheckbox = hasCheckbox;
        this.checkboxGroup = checkboxGroup;
    }
}
