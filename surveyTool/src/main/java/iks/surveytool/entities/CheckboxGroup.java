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

    void checkIfComplete() throws InvalidEntityException {
        if(checkboxes.isEmpty()) throw new InvalidEntityException("No checkboxes to choose from", this);
    }

    void validate(boolean questionRequired) throws InvalidEntityException {
        this.validateData(questionRequired);
        this.validateCheckboxes();
    }

    private void validateData(boolean questionRequired) throws InvalidEntityException {
        this.checkMinMaxSelect(questionRequired);
        this.checkNumberOfCheckboxes();
    }

    private void checkMinMaxSelect(boolean questionRequired) throws InvalidEntityException {
        if(this.minSelect > this.maxSelect) {
            throw new InvalidEntityException("Minimum Selection exceeds Maximum Selection", this);
        }
        if(this.minSelect < (questionRequired && this.multipleSelect ? 1 : 0)) {
            throw new InvalidEntityException("Minimum Selection too low", this);
        }
        if(this.maxSelect < 2) throw new InvalidEntityException("Max selection less than 2", this);
    }

    private void checkNumberOfCheckboxes() throws InvalidEntityException {
        if(this.checkboxes.size() < (this.multipleSelect ? this.maxSelect : 2)) {
            throw new InvalidEntityException("Number of options is less than the number of options that can be chosen at once", this);
        }
    }

    private void validateCheckboxes() throws InvalidEntityException {
        for (Checkbox c : this.checkboxes) {
            c.validate();
        }
    }
}
