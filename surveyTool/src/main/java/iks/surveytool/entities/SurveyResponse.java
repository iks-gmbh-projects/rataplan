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
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "surveyId", nullable = false)
    private Survey survey;
    private Long userId;
    @OneToMany(mappedBy = "response", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;

    public void validate() throws InvalidEntityException {
        if(survey == null || answers == null) throw new InvalidEntityException("missing survey or answers", this);
        if(survey.isAnonymousParticipation() || userId != null) throw new InvalidEntityException("non-anon", this);
        for(Answer a : answers) {
            a.validate();
        }
    }
}