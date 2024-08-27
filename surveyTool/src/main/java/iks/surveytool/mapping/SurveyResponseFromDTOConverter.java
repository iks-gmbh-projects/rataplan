package iks.surveytool.mapping;

import iks.surveytool.domain.QuestionType;
import iks.surveytool.domain.QuestionTypeInfo;
import iks.surveytool.dtos.AnswerDTO;
import iks.surveytool.dtos.SurveyResponseDTO;
import iks.surveytool.entities.AbstractEntity;
import iks.surveytool.entities.QuestionGroup;
import iks.surveytool.entities.Survey;
import iks.surveytool.entities.SurveyResponse;
import iks.surveytool.entities.answer.ChoiceAnswerText;
import iks.surveytool.entities.answer.OpenAnswer;
import iks.surveytool.entities.question.*;
import iks.surveytool.repositories.SurveyRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.spi.MappingEngine;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SurveyResponseFromDTOConverter implements Converter<SurveyResponseDTO, SurveyResponse> {
    @Data
    @RequiredArgsConstructor
    private static class QuestionIdentifier {
        private final long group;
        private final int rank;
    }
    
    private final SurveyRepository surveyRepository;
    @Override
    public SurveyResponse convert(MappingContext<SurveyResponseDTO, SurveyResponse> context) {
        final MappingEngine mappingEngine = context.getMappingEngine();
        final SurveyResponseDTO source = context.getSource();
        if(source == null) return null;
        final SurveyResponse dest = Objects.requireNonNullElseGet(context.getDestination(), SurveyResponse::new);
        dest.setId(source.getId());
        dest.setUserId(source.getUserId());
        Survey survey = surveyRepository.findById(source.getSurveyId()).orElseThrow();
        dest.setSurvey(survey);
        Map<QuestionIdentifier, ? extends AbstractQuestion> questions = survey.getQuestionGroups()
            .stream()
            .flatMap(g -> Arrays.stream(QuestionType.values())
                .map(QuestionType::getInfo)
                .map(QuestionTypeInfo::getGetter)
                .map(getter -> Map.entry(g.getId(), getter.apply(g))))
            .flatMap(e -> e.getValue().stream().map(q -> Map.entry(e.getKey(), q)))
            .collect(Collectors.toUnmodifiableMap(e -> new QuestionIdentifier(e.getKey(), e.getValue().getRank()),
                Map.Entry::getValue
            ));
        Map<QuestionIdentifier, QuestionType> types = questions.entrySet()
            .stream()
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, e -> e.getValue().getType()));
        Map<QuestionType, List<Map.Entry<QuestionIdentifier, AnswerDTO>>> answers = source.getAnswers()
            .entrySet()
            .stream()
            .flatMap(e -> e.getValue()
                .entrySet()
                .stream()
                .map(e2 -> Map.entry(new QuestionIdentifier(e.getKey(), e2.getKey()), e2.getValue())))
            .collect(Collectors.groupingBy(e -> types.get(e.getKey())));
        dest.setOpenAnswers(answers.getOrDefault(QuestionType.OPEN, List.of())
            .stream()
            .map(e -> {
                OpenAnswer a = mappingEngine.map(context.create(e.getValue(), OpenAnswer.class));
                a.setQuestion((OpenQuestion) questions.get(e.getKey()));
                a.setResponse(dest);
                return a;
            })
            .filter(a -> a.getText() != null && a.getText().length != 0)
            .collect(Collectors.toList())
        );
        dest.setChoiceAnswerTexts(answers.getOrDefault(QuestionType.CHOICE, List.of())
            .stream()
            .map(e -> {
                ChoiceAnswerText a = mappingEngine.map(context.create(e.getValue(), ChoiceAnswerText.class));
                a.setQuestion((ChoiceQuestion) questions.get(e.getKey()));
                a.setResponse(dest);
                return a;
            })
            .filter(a -> a.getText() != null && a.getText().length != 0)
            .collect(Collectors.toList())
        );
        Map<Long, ChoiceQuestionChoice> choiceChoices = survey.getQuestionGroups()
            .stream()
            .map(QuestionGroup::getChoiceQuestions)
            .flatMap(List::stream)
            .map(ChoiceQuestion::getChoices)
            .flatMap(List::stream)
            .collect(Collectors.toUnmodifiableMap(AbstractEntity::getId, Function.identity()));
        dest.setChoiceAnswers(answers.getOrDefault(QuestionType.CHOICE, List.of())
            .stream()
            .map(Map.Entry::getValue)
            .map(AnswerDTO::getCheckboxes)
            .filter(Objects::nonNull)
            .map(Map::entrySet)
            .flatMap(Set::stream)
            .filter(e -> e.getValue() != null && e.getValue())
            .map(Map.Entry::getKey)
            .map(choiceChoices::get)
            .collect(Collectors.toList())
        );
        Map<Long, OrderQuestionChoice> orderChoices = survey.getQuestionGroups()
            .stream()
            .map(QuestionGroup::getOrderQuestions)
            .flatMap(List::stream)
            .map(OrderQuestion::getChoices)
            .flatMap(List::stream)
            .collect(Collectors.toUnmodifiableMap(OrderQuestionChoice::getId, Function.identity()));
        dest.setOrderAnswers(answers.getOrDefault(QuestionType.ORDER, List.of())
            .stream()
            .map(Map.Entry::getValue)
            .map(AnswerDTO::getOrder)
            .filter(Objects::nonNull)
            .flatMap(List::stream)
            .filter(Objects::nonNull)
            .map(orderChoices::get)
            .collect(Collectors.toList())
        );
        return dest;
    }
}