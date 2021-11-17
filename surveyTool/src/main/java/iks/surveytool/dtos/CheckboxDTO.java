package iks.surveytool.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckboxDTO {

    private Long id;
    private String text;
    private boolean hasTextField;

    //    private List<AnswerDTO> answers;

    public CheckboxDTO(Long id, String text, boolean hasTextField) {
        this.id = id;
        this.text = text;
        this.hasTextField = hasTextField;
    }
}
