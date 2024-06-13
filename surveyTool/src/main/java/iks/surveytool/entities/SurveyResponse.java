package iks.surveytool.entities;

import iks.surveytool.entities.answer.ChoiceAnswerText;
import iks.surveytool.entities.answer.OpenAnswer;
import iks.surveytool.entities.question.*;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class SurveyResponse extends AbstractEntity {
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "surveyId", nullable = false)
    private Survey survey;
    private Long userId;
    @OneToMany(mappedBy = "response", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OpenAnswer> openAnswers = new ArrayList<>();
    @ManyToMany(cascade = CascadeType.REFRESH)
    @JoinTable(
        name = "choiceAnswer",
        joinColumns = @JoinColumn(name = "responseId"),
        inverseJoinColumns = @JoinColumn(name = "choiceId")
    )
    private List<ChoiceQuestionChoice> choiceAnswers = new ArrayList<>();
    @OneToMany(mappedBy = "response", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChoiceAnswerText> choiceAnswerTexts = new ArrayList<>();
    @ManyToMany(cascade = CascadeType.REFRESH)
    @JoinTable(
        name = "orderAnswer",
        joinColumns = @JoinColumn(name = "responseId"),
        inverseJoinColumns = @JoinColumn(name = "choiceId")
    )
    @OrderColumn(name = "rank")
    private List<OrderQuestionChoice> orderAnswers = new ArrayList<>();
    
    @Override
    public void resetId() {
        openAnswers.forEach(AbstractEntity::resetId);
        choiceAnswerTexts.forEach(AbstractEntity::resetId);
    }
    
    @Override
    public void bindChildren() {
        super.bindChildren();
        for(OpenAnswer oa : openAnswers) {
            if(oa.getResponse() == null) oa.setResponse(this);
            oa.bindChildren();
        }
        for(ChoiceAnswerText cat : choiceAnswerTexts) {
            if(cat.getResponse() == null) cat.setResponse(this);
            cat.bindChildren();
        }
    }
    
    public void validate() throws InvalidEntityException {
        if(survey == null || openAnswers == null || choiceAnswers == null || choiceAnswerTexts == null) invalid("missing survey or answers");
        if(!survey.isAnonymousParticipation() && userId == null) invalid("non-anon");
        {
            if(!validatePunctualSubmission()) throw new InvalidEntityException("Submission too late or too early",this);
        for(OpenAnswer a : openAnswers) {
                a.validate();
            }
            Map<Boolean, Set<Long>> surveyQuestionIds = survey.getQuestionGroups()
                .stream()
                .map(QuestionGroup::getOpenQuestions)
                .flatMap(List::stream)
                .collect(Collectors.partitioningBy(
                    OpenQuestion::isRequired,
                    Collectors.mapping(AbstractEntity::getId, Collectors.toUnmodifiableSet())
                ));
            Set<Long> answeredQuestions = openAnswers.stream()
                .map(OpenAnswer::getQuestion)
                .map(AbstractEntity::getId)
                .collect(Collectors.toUnmodifiableSet());
            if(!surveyQuestionIds.values()
                .stream()
                .flatMap(Set::stream)
                .collect(Collectors.toUnmodifiableSet())
                .containsAll(answeredQuestions)) invalid("external question answered");
            if(!answeredQuestions.containsAll(surveyQuestionIds.get(true))) invalid("missing required question");
        }
        {
            Map<Long, Long> selectionCount = choiceAnswers.stream()
                .map(ChoiceQuestionChoice::getQuestion)
                .collect(Collectors.groupingBy(AbstractEntity::getId, Collectors.counting()));
            List<ChoiceQuestion> questions = survey.getQuestionGroups()
                .stream()
                .map(QuestionGroup::getChoiceQuestions)
                .flatMap(List::stream)
                .collect(Collectors.toList());
            Set<Long> questionIds = questions.stream().map(AbstractEntity::getId).collect(Collectors.toUnmodifiableSet());
            if(!questionIds.containsAll(selectionCount.keySet())) {
                invalid("external question answered");
            }
            for(ChoiceQuestion q : questions) {
                long count = selectionCount.getOrDefault(q.getId(), 0L);
                if(count < q.getMinSelect()) invalid("too few selected options");
                if(count > q.getMaxSelect()) invalid("too many selected options");
            }
            Set<Long> answeredQuestionIds = choiceAnswerTexts.stream()
                .map(ChoiceAnswerText::getQuestion)
                .map(AbstractEntity::getId).collect(
                Collectors.toUnmodifiableSet());
            Set<Long> textFieldQuestionIds = choiceAnswers.stream()
                .filter(ChoiceQuestionChoice::isHasTextField)
                .map(ChoiceQuestionChoice::getQuestion)
                .map(AbstractEntity::getId)
                .collect(Collectors.toUnmodifiableSet());
            if(!questionIds.containsAll(answeredQuestionIds)) invalid("external question answered");
            if(!textFieldQuestionIds.containsAll(answeredQuestionIds)) invalid("question without text field answered with text");
        }
        {
            Map<Long, Long> selectionCount = orderAnswers.stream()
                .map(OrderQuestionChoice::getQuestion)
                .collect(Collectors.groupingBy(AbstractEntity::getId, Collectors.counting()));
            List<OrderQuestion> questions = survey.getQuestionGroups()
                .stream()
                .map(QuestionGroup::getOrderQuestions)
                .flatMap(List::stream)
                .collect(Collectors.toList());
            Set<Long> questionIds = questions.stream().map(AbstractEntity::getId).collect(Collectors.toUnmodifiableSet());
            if(!questionIds.containsAll(selectionCount.keySet())) {
                invalid("external question answered");
            }
            for(OrderQuestion q : questions) {
                long count = selectionCount.getOrDefault(q.getId(), 0L);
                if(count != q.getChoices().size()) invalid("options missing or added");
            }
        }
    }
    
    private boolean validatePunctualSubmission() {
        return Instant.now().isAfter(survey.getStartDate().toInstant()) &&
               Instant.now().isBefore(survey.getEndDate().toInstant());
    }
    
    
}
