package iks.surveytool.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Answer extends AbstractEntity {

    private String text;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "responseId", nullable = false)
    private SurveyResponse response;

    @ManyToOne
    @JoinColumn(name = "questionId", nullable = false)
    private Question question;

    @ManyToMany(cascade = {CascadeType.REFRESH})
    @JoinTable(
            name = "checkboxSelections",
            joinColumns = {
                    @JoinColumn(name = "answerId", referencedColumnName = "id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "checkboxId", referencedColumnName = "id")
            }
    )
    private List<Checkbox> checkboxes;

    public Answer(String text) {
        this.text = text;
    }

    public boolean validate() {
        if (this.response == null || this.question == null) {
            return false;
        }
        if(!Objects.equals(this.response.getSurvey().getId(), this.question.getQuestionGroup().getSurvey().getId())) {
            return false;
        }
        if(this.question.isHasCheckbox() && (this.checkboxes == null)) return false;
        if (!this.question.isHasCheckbox() || this.checkboxes.stream().anyMatch(Checkbox::isHasTextField)) {
            return this.checkIfAnswerTextValid();
        }
        return true;
    }

    private boolean checkIfAnswerTextValid() {
        return this.text != null && this.text.length() <= 1500;
    }
}
