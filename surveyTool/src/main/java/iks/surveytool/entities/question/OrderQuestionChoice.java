package iks.surveytool.entities.question;

import iks.surveytool.entities.AbstractEntity;
import iks.surveytool.entities.InvalidEntityException;
import lombok.*;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class OrderQuestionChoice extends AbstractEntity {
    @Column(nullable = false)
    private byte[] text;
    
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "questionId", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private OrderQuestion question;
    
    @Builder
    public OrderQuestionChoice(byte[] text) {
        this.text = text;
    }
    
    public void validate() throws InvalidEntityException {
        if(text == null || text.length == 0) invalid("no text");
    }
}