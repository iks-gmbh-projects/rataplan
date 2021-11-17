package iks.surveytool.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AnswerDTO {

    private Long id;
    private String text;

    private Long userID;
    private Long checkboxID;
    private Long questionID;

    public AnswerDTO(Long id, String text, Long userID) {
        this.id = id;
        this.text = text;
        this.userID = userID;
    }

    public AnswerDTO(Long id, String text, Long userID, Long checkboxID) {
        this.id = id;
        this.text = text;
        this.userID = userID;
        this.checkboxID = checkboxID;
    }
}
