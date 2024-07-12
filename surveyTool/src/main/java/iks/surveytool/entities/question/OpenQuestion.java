package iks.surveytool.entities.question;

import iks.surveytool.domain.QuestionType;
import iks.surveytool.entities.answer.OpenAnswer;
import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class OpenQuestion extends AbstractQuestion {
    private boolean required;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<OpenAnswer> answers = new ArrayList<>();

    @Builder
    public OpenQuestion(int rank, byte[] text, boolean required) {
        super(rank, text);
        this.required = required;
    }
    
    @Override
    public QuestionType getType() {
        return QuestionType.OPEN;
    }
    
    @Override
    public void checkIfComplete() {}
}