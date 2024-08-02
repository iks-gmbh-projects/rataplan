package iks.surveytool.entities.answer;

import iks.surveytool.entities.InvalidEntityException;
import iks.surveytool.entities.question.AbstractQuestion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
public abstract class AbstractTextAnswer<T extends AbstractQuestion> extends AbstractAnswer<T> {
    @Column(nullable = false)
    private byte[] text;
    
    @Override
    public void validate() throws InvalidEntityException {
        if(text == null || text.length == 0) invalid("empty answer");
    }
}