package iks.surveytool.entities;

import iks.surveytool.entities.answer.ChoiceAnswerText;
import iks.surveytool.entities.answer.OpenAnswer;
import iks.surveytool.entities.question.ChoiceQuestion;
import iks.surveytool.entities.question.ChoiceQuestionChoice;
import iks.surveytool.entities.question.OpenQuestion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
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

    public void validate() throws InvalidEntityException {
        if(survey == null || openAnswers == null || choiceAnswers == null || choiceAnswerTexts == null) invalid("missing survey or answers");
        if(!survey.isAnonymousParticipation() && userId == null) invalid("non-anon");
        {
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
    }
}