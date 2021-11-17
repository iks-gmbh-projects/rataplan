package iks.surveytool.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerDTO {

    private Long id;
    private String text;

    private UserDTO user;
    private CheckboxDTO checkbox;

    public AnswerDTO(Long id, String text, UserDTO user) {
        this.id = id;
        this.text = text;
        this.user = user;
    }

    public AnswerDTO(Long id, String text, UserDTO user, CheckboxDTO checkbox) {
        this.id = id;
        this.text = text;
        this.user = user;
        this.checkbox = checkbox;
    }
}
