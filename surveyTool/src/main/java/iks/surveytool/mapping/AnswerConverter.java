package iks.surveytool.mapping;

import iks.surveytool.dtos.AnswerDTO;
import iks.surveytool.entities.Answer;
import iks.surveytool.entities.Checkbox;
import iks.surveytool.entities.Question;
import iks.surveytool.entities.User;
import iks.surveytool.repositories.CheckboxRepository;
import iks.surveytool.repositories.QuestionRepository;
import iks.surveytool.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AnswerConverter {

    private final QuestionRepository questionRepository;
    private final CheckboxRepository checkboxRepository;
    private final UserRepository userRepository;

    public Converter<AnswerDTO, Answer> toDAO = new AbstractConverter<AnswerDTO, Answer>() {
        @Override
        protected Answer convert(AnswerDTO source) {
            return toAnswerEntity(source);
        }


        private Answer toAnswerEntity(AnswerDTO answerDTO) {
            String text = answerDTO.getText();

            Answer answer = new Answer(text);

            Long userId = answerDTO.getUserId();
            if (userId != null) {
                Optional<User> userOptional = userRepository.findById(userId);
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    answer.setUser(user);
                }
            }

            Long questionId = answerDTO.getQuestionId();
            if (questionId != null) {
                Optional<Question> questionOptional = questionRepository.findById(questionId);
                if (questionOptional.isPresent()) {
                    Question question = questionOptional.get();
                    answer.setQuestion(question);
                }
            }

            Long checkboxId = answerDTO.getCheckboxId();
            if (checkboxId != null) {
                Optional<Checkbox> checkboxOptional = checkboxRepository.findById(checkboxId);
                if (checkboxOptional.isPresent()) {
                    Checkbox checkbox = checkboxOptional.get();
                    answer.setCheckbox(checkbox);
                }
            }

            return answer;
        }
    };
}
