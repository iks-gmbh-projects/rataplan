package iks.surveytool.services;

import iks.surveytool.components.Mapper;
import iks.surveytool.dtos.AnswerDTO;
import iks.surveytool.entities.Answer;
import iks.surveytool.entities.Checkbox;
import iks.surveytool.entities.Question;
import iks.surveytool.entities.User;
import iks.surveytool.repositories.AnswerRepository;
import iks.surveytool.repositories.CheckboxRepository;
import iks.surveytool.repositories.QuestionRepository;
import iks.surveytool.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final CheckboxRepository checkboxRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;

    public List<Answer> findAnswersByQuestionId(Long questionId) {
        return answerRepository.findAllByQuestion_Id(questionId);
    }

    public List<AnswerDTO> createAnswerDtos(List<Answer> answers) {
        return mapper.toAnswerDtos(answers);
    }

    public List<AnswerDTO> createAnswerDtos(Long questionId) {
        List<Answer> answers = findAnswersByQuestionId(questionId);
        return mapper.toAnswerDtos(answers);
    }

    public List<Answer> createAnswersFromDtos(List<AnswerDTO> answerDTOList) {
        List<Answer> answers = new ArrayList<>();
        for (AnswerDTO answerDTO : answerDTOList) {
            Answer answer = mapper.createAnswerFromDto(answerDTO);

            Long userID = answerDTO.getUserID();
            Optional<User> userOptional = userRepository.findById(userID);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                answer.setUser(user);
            }

            Long questionID = answerDTO.getQuestionID();
            Optional<Question> questionOptional = questionRepository.findById(questionID);
            if (questionOptional.isPresent()) {
                Question question = questionOptional.get();
                answer.setQuestion(question);
            }

            Long checkboxID = answerDTO.getCheckboxID();
            if (checkboxID != null) {
                Optional<Checkbox> checkboxOptional = checkboxRepository.findById(checkboxID);
                if (checkboxOptional.isPresent()) {
                    Checkbox checkbox = checkboxOptional.get();
                    answer.setCheckbox(checkbox);
                }
            }
            answers.add(answer);
        }
        return answers;
    }

    public List<Answer> saveListOfAnswers(List<Answer> answerList) {
//        for (Answer answer : answerList) {
//            // Need to fetch question/checkbox/user from db for hibernate to recognize it
//            setQuestion(answer);
//            setUser(answer);
//        }
        return answerRepository.saveAll(answerList);
    }

    private void setQuestion(Answer answer) {
        Long questionID = answer.getQuestion().getId();
        Optional<Question> questionOptional = questionRepository.findById(questionID);

        if (questionOptional.isPresent()) {
            Question question = questionOptional.get();
            answer.setQuestion(question);

            if (question.isHasCheckbox()) {
                setCheckbox(answer);
            }
        }
    }

    private void setCheckbox(Answer answer) {
        Long checkboxID = answer.getCheckbox().getId();
        Optional<Checkbox> checkboxOptional = checkboxRepository.findById(checkboxID);

        if (checkboxOptional.isPresent()) {
            Checkbox checkbox = checkboxOptional.get();
            answer.setCheckbox(checkbox);
        }
    }

    private void setUser(Answer answer) {
        Long userID = answer.getUser().getId();
        Optional<User> userOptional = userRepository.findById(userID);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            answer.setUser(user);
        }
    }
}
