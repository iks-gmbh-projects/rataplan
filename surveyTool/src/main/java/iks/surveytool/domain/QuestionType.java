package iks.surveytool.domain;

import iks.surveytool.entities.question.AbstractQuestion;
import iks.surveytool.entities.question.ChoiceQuestion;
import iks.surveytool.entities.question.OpenQuestion;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum QuestionType {
    OPEN(OpenQuestion.class), CHOICE(ChoiceQuestion.class);
    public final Class<? extends AbstractQuestion> entity;
}