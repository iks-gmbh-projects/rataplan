package iks.surveytool.entities;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Data
public class CheckboxGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private boolean multipleSelect;

    private int minSelect;
    private int maxSelect;

    @OneToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @OneToMany(mappedBy = "checkboxGroup", cascade = CascadeType.ALL)
    private List<Checkbox> checkboxes;
}
