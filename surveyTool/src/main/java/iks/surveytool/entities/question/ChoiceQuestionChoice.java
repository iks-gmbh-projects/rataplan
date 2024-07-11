package iks.surveytool.entities.question;

import iks.surveytool.entities.AbstractEntity;
import iks.surveytool.entities.InvalidEntityException;
import iks.surveytool.entities.SurveyResponse;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ChoiceQuestionChoice extends AbstractEntity {
    @Column(nullable = false)
    private byte[] text;
    private boolean hasTextField;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "questionId", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private ChoiceQuestion question;
    
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "choiceAnswers")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<SurveyResponse> selectedAnswers;

    @Builder
    public ChoiceQuestionChoice(byte[] text, boolean hasTextField) {
        this.text = text;
        this.hasTextField = hasTextField;
    }
    
    public void validate() throws InvalidEntityException {
        if(text == null || text.length == 0) invalid("no text");
    }
}