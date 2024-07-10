package iks.surveytool.entities.question;

import iks.surveytool.domain.QuestionType;
import iks.surveytool.entities.answer.OpenAnswer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OpenQuestion extends AbstractQuestion {
    private boolean required;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question")
    private List<OpenAnswer> answers = new ArrayList<>();

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