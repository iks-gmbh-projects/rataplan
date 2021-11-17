package iks.surveytool.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionGroupDTO {

    private Long id;
    private String title;

    private List<QuestionDTO> questions;

    public QuestionGroupDTO(Long id, String title, List<QuestionDTO> questions) {
        this.id = id;
        this.title = title;
        this.questions = questions;
    }
}
