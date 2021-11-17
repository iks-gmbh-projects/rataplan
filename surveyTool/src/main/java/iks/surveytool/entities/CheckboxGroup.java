package iks.surveytool.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Getter
@Setter
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
