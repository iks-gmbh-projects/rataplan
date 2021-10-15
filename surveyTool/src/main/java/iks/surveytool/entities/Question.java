package iks.surveytool.entities;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Data
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String text;
    @NotNull
    private boolean required;
    @NotNull
    private boolean hasCheckbox;

    @ManyToOne
    @JoinColumn(name = "question_group_id")
    private QuestionGroup questionGroup;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Answer> answers;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL)
    private CheckboxGroup checkboxGroup;
}
