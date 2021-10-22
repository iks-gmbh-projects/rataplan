package iks.surveytool.entities;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Data
public class CheckboxGroup extends AbstractEntity {

    @NotNull
    private boolean multipleSelect;

    private int minSelect;
    private int maxSelect;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "question_id")
    private Question question;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "checkbox_group_id")
    private List<Checkbox> checkboxes;
}
