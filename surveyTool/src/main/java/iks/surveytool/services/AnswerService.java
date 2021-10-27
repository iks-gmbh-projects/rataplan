package iks.surveytool.services;

import iks.surveytool.entities.Answer;
import iks.surveytool.entities.Question;
import iks.surveytool.repositories.AnswerRepository;
import iks.surveytool.repositories.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    public void saveListOfAnswers(List<Answer> answerList) {
        // Need to fetch question from db for hibernate to recognize it
        for (Answer answer : answerList) {
            Long questionID = answer.getQuestion().getId();
            Optional<Question> questionOptional = questionRepository.findById(questionID);
            if (questionOptional.isPresent()) {
                answer.setQuestion(questionOptional.get());
            }
        }
        answerRepository.saveAll(answerList);
    }
}
