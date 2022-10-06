package iks.surveytool.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SurveyResponse extends AbstractEntity {
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "surveyId", nullable = false)
    private Survey survey;
    private Long userId;
    @OneToMany(mappedBy = "response", cascade = CascadeType.ALL)
    private List<Answer> answers;

    public boolean validate() {
        return survey != null && answers != null && validateNonAnon() && validateAnswers();
    }

    private boolean validateNonAnon() {
        return survey.isAnonymousParticipation() || userId != null;
    }

    private boolean validateAnswers() {
        return this.answers.stream().allMatch(Answer::validate);
    }
}
