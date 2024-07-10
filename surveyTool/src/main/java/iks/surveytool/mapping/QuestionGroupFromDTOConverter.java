package iks.surveytool.mapping;

import iks.surveytool.domain.QuestionType;
import iks.surveytool.dtos.QuestionGroupDTO;
import iks.surveytool.entities.EncryptedString;
import iks.surveytool.entities.QuestionGroup;
import iks.surveytool.entities.question.AbstractQuestion;

import org.modelmapper.Converter;
import org.modelmapper.TypeToken;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.spi.MappingEngine;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class QuestionGroupFromDTOConverter implements Converter<QuestionGroupDTO, QuestionGroup> {
    @Override
    public QuestionGroup convert(MappingContext<QuestionGroupDTO, QuestionGroup> context) {
        final MappingEngine mappingEngine = context.getMappingEngine();
        final QuestionGroupDTO source = context.getSource();
        if(source == null) return null;
        final QuestionGroup dest = Objects.requireNonNullElseGet(context.getDestination(), QuestionGroup::new);
        dest.setId(source.getId());
        dest.setTitle(mappingEngine.map(context.create(source.getTitle(), EncryptedString.class)));
        List<? extends AbstractQuestion> questions = mappingEngine.map(context.create(
            source.getQuestions(),
            new TypeToken<List<AbstractQuestion>>() {}.getType()
        ));
        ListIterator<? extends AbstractQuestion> it = questions.listIterator();
        while(it.hasNext()) {
            int i = it.nextIndex();
            AbstractQuestion q = it.next();
            q.setRank(i);
            q.setQuestionGroup(dest);
        }
        Map<QuestionType, ? extends List<? extends AbstractQuestion>> questionMap = questions.stream()
            .collect(Collectors.groupingBy(AbstractQuestion::getType));
        for(QuestionType type : QuestionType.values()) {
            switch(type) {
                case OPEN:
                    dest.setOpenQuestions(mappingEngine.map(context.create(
                        Objects.requireNonNullElseGet(questionMap.get(type), ArrayList::new),
                        dest.getOpenQuestions()
                    )));
                    break;
                case CHOICE:
                    dest.setChoiceQuestions(mappingEngine.map(context.create(
                        Objects.requireNonNullElseGet(questionMap.get(type), ArrayList::new),
                        dest.getChoiceQuestions()
                    )));
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown question type: "+type);
            }
        }
        return dest;
    }
}