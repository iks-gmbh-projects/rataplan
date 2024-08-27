package iks.surveytool.domain;

import iks.surveytool.entities.QuestionGroup;
import iks.surveytool.entities.question.ChoiceQuestion;
import iks.surveytool.entities.question.OpenQuestion;
import iks.surveytool.entities.question.OrderQuestion;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestionType {
    OPEN(new QuestionTypeInfo<>(OpenQuestion.class, QuestionGroup::getOpenQuestions)),
    CHOICE(new QuestionTypeInfo<>(ChoiceQuestion.class, QuestionGroup::getChoiceQuestions)),
    ORDER(new QuestionTypeInfo<>(OrderQuestion.class, QuestionGroup::getOrderQuestions));
    public final QuestionTypeInfo<?> info;
}