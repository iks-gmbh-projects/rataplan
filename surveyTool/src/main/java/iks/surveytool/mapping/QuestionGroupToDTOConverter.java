package iks.surveytool.mapping;

import iks.surveytool.domain.QuestionType;
import iks.surveytool.domain.QuestionTypeInfo;
import iks.surveytool.dtos.QuestionDTO;
import iks.surveytool.dtos.QuestionGroupDTO;
import iks.surveytool.entities.QuestionGroup;
import iks.surveytool.entities.question.AbstractQuestion;

import org.modelmapper.Converter;
import org.modelmapper.TypeToken;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.spi.MappingEngine;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class QuestionGroupToDTOConverter implements Converter<QuestionGroup, QuestionGroupDTO> {
    @Override
    public QuestionGroupDTO convert(MappingContext<QuestionGroup, QuestionGroupDTO> context) {
        final MappingEngine mappingEngine = context.getMappingEngine();
        final QuestionGroup source = context.getSource();
        if(source == null) return null;
        final QuestionGroupDTO dest = Objects.requireNonNullElseGet(context.getDestination(), QuestionGroupDTO::new);
        dest.setId(source.getId());
        dest.setTitle(mappingEngine.map(context.create(source.getTitle(), String.class)));
        List<? extends AbstractQuestion> questions = Arrays.stream(QuestionType.values())
            .map(QuestionType::getInfo)
            .map(QuestionTypeInfo::getGetter)
            .map(f -> f.apply(source))
            .flatMap(List::stream)
            .sorted(Comparator.comparingInt(AbstractQuestion::getRank))
            .collect(Collectors.toUnmodifiableList());
        dest.setQuestions(mappingEngine.map(context.create(
            questions,
            new TypeToken<List<QuestionDTO>>() {}.getType()
        )));
        return dest;
    }
}