package iks.surveytool.services;

import iks.surveytool.components.Mapper;
import iks.surveytool.dtos.AnswerDTO;
import iks.surveytool.entities.Answer;
import iks.surveytool.entities.Checkbox;
import iks.surveytool.entities.Question;
import iks.surveytool.entities.User;
import iks.surveytool.repositories.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final Mapper mapper;

    public List<Answer> findAnswersByQuestionId(Long questionId) {
        return answerRepository.findAllByQuestion_Id(questionId);
    }

    public List<AnswerDTO> createAnswerDtos(List<Answer> answers) {
        return mapper.toAnswerDtoList(answers);
    }

    public List<AnswerDTO> createAnswerDtos(Long questionId) {
        List<Answer> answers = findAnswersByQuestionId(questionId);
        return mapper.toAnswerDtoList(answers);
    }

    public List<Answer> createAnswersFromDtos(List<AnswerDTO> answerDTOList) {
        List<Answer> answers = new ArrayList<>();
        for (AnswerDTO answerDTO : answerDTOList) {
            Answer answer = mapper.createAnswerFromDto(answerDTO);
            answers.add(answer);
        }
        return answers;
    }

    public List<Answer> saveListOfAnswers(List<Answer> answerList) {
        return answerRepository.saveAll(answerList);
    }

    public boolean validate(List<Answer> answerList) {
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

    private boolean checkIfAnswerTextValid(Answer answer) {
        String text = answer.getText();
        return text != null && text.length() <= 1500;
    }
}
