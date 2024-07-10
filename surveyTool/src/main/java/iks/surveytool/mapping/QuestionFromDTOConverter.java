package iks.surveytool.mapping;

import iks.surveytool.dtos.QuestionDTO;
import iks.surveytool.entities.question.AbstractQuestion;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.spi.MappingEngine;
import org.springframework.stereotype.Component;

@Component
public class QuestionFromDTOConverter implements Converter<QuestionDTO, AbstractQuestion> {
    @Override
    public AbstractQuestion convert(MappingContext<QuestionDTO, AbstractQuestion> context) {
        final MappingEngine mappingEngine = context.getMappingEngine();
        final QuestionDTO source = context.getSource();
        if(source == null) return null;
        return mappingEngine.map(context.create(source, source.getType().entity));
    }
}