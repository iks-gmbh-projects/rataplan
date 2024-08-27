package iks.surveytool.domain;

import iks.surveytool.entities.QuestionGroup;
import iks.surveytool.entities.question.AbstractQuestion;
import lombok.Data;

import java.util.List;
import java.util.function.Function;

@Data
public class QuestionTypeInfo<T extends AbstractQuestion> {
    public final Class<T> questionClass;
    public final Function<? super QuestionGroup, ? extends List<T>> getter;
}