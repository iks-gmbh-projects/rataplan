package iks.surveytool.entities.question;

import iks.surveytool.domain.QuestionType;
import iks.surveytool.entities.AbstractEntity;
import iks.surveytool.entities.InvalidEntityException;
import iks.surveytool.entities.QuestionGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
public abstract class AbstractQuestion extends AbstractEntity {
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "questionGroupId", nullable = false)
    private QuestionGroup questionGroup;
    private int rank;
    @Column(nullable = false)
    @NotNull
    private byte[] text;
    
    public AbstractQuestion(int rank, byte[] text) {
        this.rank = rank;
        this.text = text;
    }
    
    public abstract QuestionType getType();
    
    public abstract void checkIfComplete() throws InvalidEntityException;
    
    public void validate() throws InvalidEntityException {
        if(this.text == null) invalid("question text missing");
        if(this.text.length > 1024) invalid("question text too long");
    }
}