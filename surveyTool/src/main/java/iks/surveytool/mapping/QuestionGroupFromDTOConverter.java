package iks.surveytool.mapping;

import iks.surveytool.domain.QuestionType;
import iks.surveytool.dtos.QuestionDTO;
import iks.surveytool.dtos.QuestionGroupDTO;
import iks.surveytool.entities.EncryptedString;
import iks.surveytool.entities.QuestionGroup;
import iks.surveytool.entities.question.AbstractQuestion;
import iks.surveytool.entities.question.ChoiceQuestion;
import iks.surveytool.entities.question.OpenQuestion;

import org.modelmapper.Converter;
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
        ListIterator<QuestionDTO> it = source.getQuestions().listIterator();
        while(it.hasNext()) {
            int i = it.nextIndex();
            QuestionDTO q = it.next();
            q.setRank(i);
        }
        Map<QuestionType, ? extends List<? extends QuestionDTO>> questionMap = source.getQuestions()
            .stream()
            .collect(Collectors.groupingBy(QuestionDTO::getType));
        for(QuestionType type : QuestionType.values()) {
            List<? extends QuestionDTO> sourceQuestions = Objects.requireNonNullElseGet(
                questionMap.get(type),
                List::of
            );
            switch(type) {
                case OPEN:
                    transfer(context, dest, sourceQuestions, dest.getOpenQuestions(), OpenQuestion.class);
                    break;
                case CHOICE:
                    transfer(context, dest, sourceQuestions, dest.getChoiceQuestions(), ChoiceQuestion.class);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown question type: "+type);
            }
        }
        return dest;
    }
    
    private <T extends AbstractQuestion> void transfer(MappingContext<?, ?> context, QuestionGroup dest, List<? extends QuestionDTO> sourceQuestions, List<T> destQuestions, Class<T> questionClass) {
        MappingEngine mappingEngine = context.getMappingEngine();
        Iterator<? extends QuestionDTO> sIt = sourceQuestions.iterator();
        ListIterator<T> dIt = destQuestions.listIterator();
        while(sIt.hasNext() && dIt.hasNext()) {
            QuestionDTO sourceQuestion = sIt.next();
            T destQuestion = dIt.next();
            sourceQuestion.setId(destQuestion.getId());
            T result = mappingEngine.map(context.create(sourceQuestion, destQuestion));
            if(result != destQuestion) {
                result.setId(null);
                result.setQuestionGroup(dest);
                dIt.set(result);
            }
        }
        while(dIt.hasNext()) {
            dIt.next();
            dIt.remove();
        }
        while(sIt.hasNext()) {
            QuestionDTO sourceQuestion = sIt.next();
            sourceQuestion.setId(null);
            T result = mappingEngine.map(context.create(sourceQuestion, questionClass));
            result.setQuestionGroup(dest);
            dIt.add(result);
        }
    }
}