package iks.surveytool.entities;

import iks.surveytool.mapping.crypto.DBEncryptedStringConverter;
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
    
    @Convert(converter = DBEncryptedStringConverter.class)
    private EncryptedString text;

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

    public Answer(EncryptedString text) {
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
        if(this.question.isHasCheckbox()) {
            CheckboxGroup grp = this.question.getCheckboxGroup();
            int count = this.checkboxes.size();
            if(grp.isMultipleSelect()) {
                if(grp.getMinSelect() > count || grp.getMaxSelect() < count) return false;
            } else {
                if(count > 1 || (count < 1 && question.isRequired())) return false;
            }
        }
        if (!this.question.isHasCheckbox() || this.checkboxes.stream().anyMatch(Checkbox::isHasTextField)) {
            return this.checkIfAnswerTextValid();
        }
        return true;
    }

    private boolean checkIfAnswerTextValid() {
        return this.text != null && this.text.getString().length() <= 1500;
    }
}
