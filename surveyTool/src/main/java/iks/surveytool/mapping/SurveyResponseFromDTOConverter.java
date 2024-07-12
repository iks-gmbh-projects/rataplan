package iks.surveytool.mapping;

import iks.surveytool.domain.QuestionType;
import iks.surveytool.dtos.AnswerDTO;
import iks.surveytool.dtos.SurveyResponseDTO;
import iks.surveytool.entities.AbstractEntity;
import iks.surveytool.entities.QuestionGroup;
import iks.surveytool.entities.Survey;
import iks.surveytool.entities.SurveyResponse;
import iks.surveytool.entities.answer.ChoiceAnswerText;
import iks.surveytool.entities.answer.OpenAnswer;
import iks.surveytool.entities.question.AbstractQuestion;
import iks.surveytool.entities.question.ChoiceQuestion;
import iks.surveytool.entities.question.ChoiceQuestionChoice;
import iks.surveytool.entities.question.OpenQuestion;
import iks.surveytool.repositories.SurveyRepository;
import lombok.RequiredArgsConstructor;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.spi.MappingEngine;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class SurveyResponseFromDTOConverter implements Converter<SurveyResponseDTO, SurveyResponse> {
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
        Map<Long, ? extends AbstractQuestion> questions = survey.getQuestionGroups()
            .stream()
            .flatMap(g -> Stream.of(
                g.getOpenQuestions(),
                g.getChoiceQuestions()
            ))
            .flatMap(List::stream)
            .collect(Collectors.toUnmodifiableMap(
                AbstractEntity::getId,
                Function.identity()
            ));
        Map<Long, QuestionType> types = survey.getQuestionGroups()
            .stream()
            .flatMap(g -> Stream.of(g.getOpenQuestions(), g.getChoiceQuestions())
                .flatMap(Collection::stream)
            )
            .collect(Collectors.toUnmodifiableMap(AbstractEntity::getId, AbstractQuestion::getType));
        Map<QuestionType, List<Map.Entry<Long, AnswerDTO>>> answers = source.getAnswers()
            .entrySet()
            .stream()
            .collect(Collectors.groupingBy(
                e -> types.get(e.getKey())
            ));
        dest.setOpenAnswers(answers.getOrDefault(QuestionType.OPEN, List.of())
            .stream()
            .map(e -> {
                OpenAnswer a = mappingEngine.map(context.create(e.getValue(), OpenAnswer.class));
                a.setQuestion((OpenQuestion) questions.get(e.getKey()));
                a.setResponse(dest);
                return a;
            })
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
        Map<Long, ChoiceQuestionChoice> choices = survey.getQuestionGroups()
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
            .map(choices::get)
            .collect(Collectors.toList())
        );
        return dest;
    }
}