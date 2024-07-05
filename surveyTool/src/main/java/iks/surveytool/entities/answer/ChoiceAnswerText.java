package iks.surveytool.entities.answer;

import iks.surveytool.entities.InvalidEntityException;
import iks.surveytool.entities.question.ChoiceQuestion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ChoiceAnswerText extends AbstractAnswer {
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "questionId", nullable = false)
    private ChoiceQuestion question;
    @Column(nullable = false)
    private byte[] text;
    
    @Override
    public void validate() throws InvalidEntityException {
        if(text == null || text.length == 0) invalid("empty answer");
    }
}