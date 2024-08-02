package iks.surveytool.entities.answer;

import iks.surveytool.entities.question.OpenQuestion;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class OpenAnswer extends AbstractTextAnswer<OpenQuestion> {}