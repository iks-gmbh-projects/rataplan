package iks.surveytool.mapping;

import iks.surveytool.dtos.AnswerDTO;
import iks.surveytool.dtos.SurveyResponseDTO;
import iks.surveytool.entities.SurveyResponse;
import iks.surveytool.entities.question.ChoiceQuestionChoice;
import iks.surveytool.entities.question.OrderQuestionChoice;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.spi.MappingEngine;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class SurveyResponseToDTOConverter implements Converter<SurveyResponse, SurveyResponseDTO> {
    @Override
    public SurveyResponseDTO convert(MappingContext<SurveyResponse, SurveyResponseDTO> context) {
        final MappingEngine mappingEngine = context.getMappingEngine();
        final SurveyResponse source = context.getSource();
        if(source == null) return null;
        final SurveyResponseDTO dest = Objects.requireNonNullElseGet(context.getDestination(), SurveyResponseDTO::new);
        dest.setId(source.getId());
        dest.setUserId(source.getUserId());
        dest.setSurveyId(source.getSurvey().getId());
        Map<Long, Map<Integer, AnswerDTO>> answers = new HashMap<>();
        answers.putAll(Stream.concat(
            source.getOpenAnswers().stream(),
            source.getChoiceAnswerTexts().stream()
        ).collect(Collectors.groupingBy(
                a -> a.getQuestion().getQuestionGroup().getId(),
                Collectors.toMap(
                    a -> a.getQuestion().getRank(),
                    a -> mappingEngine.map(context.create(a, AnswerDTO.class))
                )
            )));
        for(ChoiceQuestionChoice choice : source.getChoiceAnswers()) {
            AnswerDTO answer = answers.computeIfAbsent(choice.getQuestion().getQuestionGroup().getId(), i -> new HashMap<>())
                .computeIfAbsent(choice.getQuestion().getRank(), i -> new AnswerDTO());
            Map<Long, Boolean> checkboxes = answer.getCheckboxes();
            if(checkboxes == null) {
                checkboxes = new HashMap<>();
                answer.setCheckboxes(checkboxes);
            }
            answer.getCheckboxes()
                .put(choice.getId(), true);
        }
        source.getOrderAnswers()
            .stream()
            .collect(Collectors.groupingBy(
                OrderQuestionChoice::getQuestion,
                Collectors.mapping(
                    OrderQuestionChoice::getId,
                    Collectors.toList()
                )
            ))
            .forEach((q, o) -> answers.computeIfAbsent(q.getQuestionGroup().getId(), i -> new HashMap<>())
                .put(q.getRank(), new AnswerDTO(o))
            );
        dest.setAnswers(answers);
        return dest;
    }
}