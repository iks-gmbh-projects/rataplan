package iks.surveytool.entities.question;

import iks.surveytool.domain.QuestionType;
import iks.surveytool.entities.AbstractEntity;
import iks.surveytool.entities.InvalidEntityException;
import iks.surveytool.entities.answer.ChoiceAnswerText;
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
public class ChoiceQuestion extends AbstractQuestion {
    private int minSelect;
    private int maxSelect;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question", orphanRemoval = true)
    private List<ChoiceQuestionChoice> choices = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question", orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<ChoiceAnswerText> textAnswers = new ArrayList<>();

    public ChoiceQuestion(int rank, byte[] text, int minSelect, int maxSelect) {
        super(rank, text);
        this.minSelect = minSelect;
        this.maxSelect = maxSelect;
    }
    
    @Override
    public void resetId() {
        super.resetId();
        choices.forEach(AbstractEntity::resetId);
    }
    
    @Override
    public QuestionType getType() {
        return QuestionType.CHOICE;
    }
    
    @Override
    public void checkIfComplete() throws InvalidEntityException {
        if(choices.isEmpty()) invalid("no choices");
    }
    
    @Override
    public void validate() throws InvalidEntityException {
        super.validate();
        if(minSelect < 0) invalid("minSelect < 0");
        if(maxSelect < minSelect) invalid("maxSelect < minSelect");
        if(maxSelect > choices.size()) invalid("maxSelect > choices");
        for(ChoiceQuestionChoice choice : choices) choice.validate();
    }
}