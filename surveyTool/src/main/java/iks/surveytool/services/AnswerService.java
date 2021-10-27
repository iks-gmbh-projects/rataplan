package iks.surveytool.services;

import iks.surveytool.entities.Answer;
import iks.surveytool.entities.Checkbox;
import iks.surveytool.entities.Question;
import iks.surveytool.repositories.AnswerRepository;
import iks.surveytool.repositories.CheckboxRepository;
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
    private final CheckboxRepository checkboxRepository;

    public void saveListOfAnswers(List<Answer> answerList) {
        // Need to fetch question from db for hibernate to recognize it
        for (Answer answer : answerList) {
            Long questionID = answer.getQuestion().getId();
            Optional<Question> questionOptional = questionRepository.findById(questionID);

            if (questionOptional.isPresent()) {
                Question question = questionOptional.get();
                answer.setQuestion(question);

                if (question.isHasCheckbox()) {
                    Long checkboxID = answer.getCheckbox().getId();
                    Optional<Checkbox> checkboxOptional = checkboxRepository.findById(checkboxID);

                    if (checkboxOptional.isPresent()) {
                        Checkbox checkbox = checkboxOptional.get();
                        answer.setCheckbox(checkbox);
                    }
                }

            }
        }
        answerRepository.saveAll(answerList);
    }
}
