package iks.surveytool.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CheckboxGroup extends AbstractEntity {

    @NotNull
    private boolean multipleSelect;

    private int minSelect;
    private int maxSelect;

    @OneToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "questionId", nullable = false)
    private Question question;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "checkboxGroup")
    private List<Checkbox> checkboxes;

    public CheckboxGroup(boolean multipleSelect, int minSelect, int maxSelect, List<Checkbox> checkboxes) {
        this.multipleSelect = multipleSelect;
        this.minSelect = minSelect;
        this.maxSelect = maxSelect;
        this.checkboxes = checkboxes;
    }

    void checkIfComplete() throws InvalidSurveyException {
        if(checkboxes.isEmpty()) throw new InvalidSurveyException("No checkboxes to choose from");
    }

    void validate(boolean questionRequired) throws InvalidSurveyException {
        this.validateData(questionRequired);
        this.validateCheckboxes();
    }

    private void validateData(boolean questionRequired) throws InvalidSurveyException {
        this.checkMinMaxSelect(questionRequired);
        this.checkNumberOfCheckboxes();
    }

    private void checkMinMaxSelect(boolean questionRequired) throws InvalidSurveyException {
        if(this.minSelect > this.maxSelect) {
            throw new InvalidSurveyException("Minimum Selection exceeds Maximum Selection");
        }
        if(this.minSelect < (questionRequired && this.multipleSelect ? 1 : 0)) {
            throw new InvalidSurveyException("Minimum Selection too low");
        }
        if(this.maxSelect < 2) throw new InvalidSurveyException("Max selection less than 2");
    }

    private void checkNumberOfCheckboxes() throws InvalidSurveyException {
        if(this.checkboxes.size() < (this.multipleSelect ? this.maxSelect : 2)) {
            throw new InvalidSurveyException("Number of options is less than the number of options that can be chosen at once");
        }
    }

    private void validateCheckboxes() throws InvalidSurveyException {
        for (Checkbox c : this.checkboxes) {
            c.validate();
        }
    }
}
