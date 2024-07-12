package iks.surveytool.entities.answer;

import iks.surveytool.entities.InvalidEntityException;
import iks.surveytool.entities.question.OpenQuestion;
import lombok.*;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class OpenAnswer extends AbstractAnswer {
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "questionId", nullable = false)
    private OpenQuestion question;
    @Column(nullable = false)
    private byte[] text;
    
    @Override
    public void validate() throws InvalidEntityException {
        if(text == null || text.length == 0) invalid("empty answer");
    }
}