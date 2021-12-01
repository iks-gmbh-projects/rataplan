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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "questionId", nullable = false)
    private Question question;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "checkboxGroupId")
    private List<Checkbox> checkboxes;

    public CheckboxGroup(boolean multipleSelect, int minSelect, int maxSelect, List<Checkbox> checkboxes) {
        this.multipleSelect = multipleSelect;
        this.minSelect = minSelect;
        this.maxSelect = maxSelect;
        this.checkboxes = checkboxes;
    }

    boolean checkIfComplete() {
        return !checkboxes.isEmpty();
    }

    boolean validate(boolean questionRequired) {
        return this.validateData(questionRequired) && this.validateCheckboxes();
    }

    private boolean validateData(boolean questionRequired) {
        return this.checkMinMaxSelect(questionRequired) && this.checkNumberOfCheckboxes();
    }

    private boolean checkMinMaxSelect(boolean questionRequired) {
        return this.minSelect <= this.maxSelect
                && this.minSelect >= 0
                && this.maxSelect >= 2
                && !(questionRequired && this.multipleSelect && this.minSelect < 1);
    }

    private boolean checkNumberOfCheckboxes() {
        int numberOfCheckboxes = this.checkboxes.size();
        return (!this.multipleSelect && numberOfCheckboxes >= 2) ||
                (this.multipleSelect && numberOfCheckboxes >= this.maxSelect);
    }

    private boolean validateCheckboxes() {
        for (Checkbox checkbox : this.checkboxes) {
            if (!checkbox.validate()) {
                return false;
            }
        }
        return true;
    }
}
