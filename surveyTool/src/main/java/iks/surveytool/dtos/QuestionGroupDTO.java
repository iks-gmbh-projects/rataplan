package iks.surveytool.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class QuestionGroupDTO extends AbstractDTO {

    private String title;

    private List<QuestionDTO> questions;

    public QuestionGroupDTO(Long id, String title, List<QuestionDTO> questions) {
        super(id);
        this.title = title;
        this.questions = questions;
    }
    
    @Override
    public boolean valid() {
        return title != null && !title.isBlank() && questions.stream().allMatch(QuestionDTO::valid);
    }
}
