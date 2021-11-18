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
    private String userName;

    private Long checkboxID;
    private Long questionID;

    public AnswerDTO(Long id, String text, Long userID, String userName) {
        this.id = id;
        this.text = text;
        this.userID = userID;
        this.userName = userName;
    }

    public AnswerDTO(Long id, String text, Long userID, String userName, Long checkboxID) {
        this.id = id;
        this.text = text;
        this.userID = userID;
        this.userName = userName;
        this.checkboxID = checkboxID;
    }
}
