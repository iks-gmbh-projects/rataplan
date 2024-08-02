package iks.surveytool.entities.answer;

import iks.surveytool.entities.question.ChoiceQuestion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ChoiceAnswerText extends AbstractTextAnswer<ChoiceQuestion> {}