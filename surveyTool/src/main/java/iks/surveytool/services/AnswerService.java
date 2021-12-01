package iks.surveytool.services;

import iks.surveytool.components.Mapper;
import iks.surveytool.dtos.AnswerDTO;
import iks.surveytool.entities.Answer;
import iks.surveytool.entities.Checkbox;
import iks.surveytool.entities.Question;
import iks.surveytool.entities.User;
import iks.surveytool.repositories.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final Mapper mapper;

    public ResponseEntity<List<AnswerDTO>> processAnswerDTOs(AnswerDTO[] answerDTOs) {
        List<AnswerDTO> answerDTOList = Arrays.asList(answerDTOs);
        List<Answer> answerList = createAnswersFromDTOs(answerDTOList);
        if (validate(answerList)) {
            List<Answer> savedAnswers = saveAnswers(answerList);
            List<AnswerDTO> savedAnswerDTOs = createAnswerDTOs(savedAnswers);
            return ResponseEntity.ok(savedAnswerDTOs);
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
    }

    private List<Answer> createAnswersFromDTOs(List<AnswerDTO> answerDTOList) {
        List<Answer> answers = new ArrayList<>();
        for (AnswerDTO answerDTO : answerDTOList) {
            Answer answer = mapper.createAnswerFromDTO(answerDTO);
            answers.add(answer);
        }
        return answers;
    }

    private boolean validate(List<Answer> answerList) {
        for (Answer answer : answerList) {
            if (!validateAnswer(answer)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateAnswer(Answer answer) {
        User user = answer.getUser();
        Question question = answer.getQuestion();
        Checkbox checkbox = answer.getCheckbox();
        if (user == null || question == null || (question.isHasCheckbox() && checkbox == null)) {
            return false;
        }
        if (!question.isHasCheckbox() || checkbox.isHasTextField()) {
            return checkIfAnswerTextValid(answer);
        }
        return true;
    }

    private List<Answer> saveAnswers(List<Answer> answerList) {
        return answerRepository.saveAll(answerList);
    }

    private boolean checkIfAnswerTextValid(Answer answer) {
        String text = answer.getText();
        return text != null && text.length() <= 1500;
    }

    private List<AnswerDTO> createAnswerDTOs(List<Answer> answers) {
        return mapper.toAnswerDTOList(answers);
    }

    public ResponseEntity<List<AnswerDTO>> processAnswersByQuestionId(Long questionId) {
        List<AnswerDTO> answerDTOs = createAnswerDTOsByQuestionId(questionId);
        return ResponseEntity.ok(answerDTOs);
    }

    private List<AnswerDTO> createAnswerDTOsByQuestionId(Long questionId) {
        List<Answer> answers = findAnswersByQuestionId(questionId);
        return mapper.toAnswerDTOList(answers);
    }

    private List<Answer> findAnswersByQuestionId(Long questionId) {
        return answerRepository.findAllByQuestion_Id(questionId);
    }
}
