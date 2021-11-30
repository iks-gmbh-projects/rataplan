package iks.surveytool.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AnswerDTO extends AbstractDTO {

    private String text;

    private Long userId;
    private String userName;

    private Long checkboxId;
    private Long questionId;

    public AnswerDTO(Long id, String text, Long userId, String userName) {
        super(id);
        this.text = text;
        this.userId = userId;
        this.userName = userName;
    }

    public AnswerDTO(Long id, String text, Long userId, String userName, Long checkboxId) {
        super(id);
        this.text = text;
        this.userId = userId;
        this.userName = userName;
        this.checkboxId = checkboxId;
    }
}
