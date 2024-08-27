package iks.surveytool.entities.question;

import iks.surveytool.domain.QuestionType;
import iks.surveytool.entities.AbstractEntity;
import iks.surveytool.entities.InvalidEntityException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
public class OrderQuestion extends AbstractQuestion {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "question", orphanRemoval = true)
    private List<OrderQuestionChoice> choices = new ArrayList<>();
    
    @Override
    public void resetId() {
        super.resetId();
        choices.forEach(AbstractEntity::resetId);
    }
    
    @Override
    public void bindChildren() {
        super.bindChildren();
        for(OrderQuestionChoice oqc : choices) {
            if(oqc.getQuestion() == null) oqc.setQuestion(this);
            oqc.bindChildren();
        }
    }
    
    @Override
    public QuestionType getType() {
        return QuestionType.ORDER;
    }
    @Override
    public void checkIfComplete() throws InvalidEntityException {
        if(choices.isEmpty()) invalid("no choices");
    }
    @Override
    public void validate() throws InvalidEntityException {
        super.validate();
        for(OrderQuestionChoice choice : choices) choice.validate();
    }
}