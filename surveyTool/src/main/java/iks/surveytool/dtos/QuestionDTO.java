package iks.surveytool.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionDTO {

    private Long id;
    private String text;
    private boolean required;
    private boolean hasCheckbox;

//    private List<AnswerDTO> answers;

    private CheckboxGroupDTO checkboxGroup;

    public QuestionDTO(Long id, String text, boolean required, boolean hasCheckbox) {
        this.id = id;
        this.text = text;
        this.required = required;
        this.hasCheckbox = hasCheckbox;
    }

    public QuestionDTO(Long id, String text, boolean required, boolean hasCheckbox, CheckboxGroupDTO checkboxGroup) {
        this.id = id;
        this.text = text;
        this.required = required;
        this.hasCheckbox = hasCheckbox;
        this.checkboxGroup = checkboxGroup;
    }
}
