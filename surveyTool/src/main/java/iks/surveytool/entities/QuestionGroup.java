package iks.surveytool.entities;

import iks.surveytool.entities.question.ChoiceQuestion;
import iks.surveytool.entities.question.OpenQuestion;
import iks.surveytool.entities.question.OrderQuestion;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class QuestionGroup extends AbstractEntity {
    
    private byte[] title;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "surveyId", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Survey survey;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "questionGroup")
    @OrderBy("rank")
    private List<OpenQuestion> openQuestions = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "questionGroup")
    @OrderBy("rank")
    private List<ChoiceQuestion> choiceQuestions = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "questionGroup")
    @OrderBy("rank")
    private List<OrderQuestion> orderQuestions = new ArrayList<>();

    @Builder
    public QuestionGroup(
        byte[] title,
        List<OpenQuestion> openQuestions,
        List<ChoiceQuestion> choiceQuestions,
        List<OrderQuestion> orderQuestions
    ) {
        this.title = title;
        this.openQuestions = openQuestions;
        this.choiceQuestions = choiceQuestions;
        this.orderQuestions = orderQuestions;
    }
    
    
    @Override
    public void resetId() {
        super.resetId();
        openQuestions.forEach(AbstractEntity::resetId);
        choiceQuestions.forEach(AbstractEntity::resetId);
        orderQuestions.forEach(AbstractEntity::resetId);
    }
    
    @Override
    public void bindChildren() {
        super.bindChildren();
        for(OpenQuestion oq : openQuestions) {
            if(oq.getQuestionGroup() == null) oq.setQuestionGroup(this);
            oq.bindChildren();
        }
        for(ChoiceQuestion cq : choiceQuestions) {
            if(cq.getQuestionGroup() == null) cq.setQuestionGroup(this);
            cq.bindChildren();
        }
        for(OrderQuestion oq : orderQuestions) {
            if(oq.getQuestionGroup() == null) oq.setQuestionGroup(this);
            oq.bindChildren();
        }
    }
    
    void checkIfComplete() throws InvalidEntityException {
        if(this.openQuestions.isEmpty() &&
           this.choiceQuestions.isEmpty() &&
           this.orderQuestions.isEmpty()
        ) throw new InvalidEntityException("Empty question group", this);
        this.checkIfQuestionsComplete();
    }

    private void checkIfQuestionsComplete() throws InvalidEntityException {
        for(OpenQuestion q:this.openQuestions) {
            q.checkIfComplete();
        }
        for(ChoiceQuestion q:this.choiceQuestions) {
            q.checkIfComplete();
        }
        for(OrderQuestion q:this.orderQuestions) {
            q.checkIfComplete();
        }
    }

    void validate() throws InvalidEntityException {
        this.validateData();
        this.validateQuestions();
    }

    private void validateData() throws InvalidEntityException {
        if(this.title == null) throw new InvalidEntityException("Question group without title", this);
    }

    private void validateQuestions() throws InvalidEntityException {
        for(OpenQuestion q:this.openQuestions) {
            q.validate();
        }
        for(ChoiceQuestion q:this.choiceQuestions) {
            q.validate();
        }
        for(OrderQuestion q:this.orderQuestions) {
            q.validate();
        }
    }
}