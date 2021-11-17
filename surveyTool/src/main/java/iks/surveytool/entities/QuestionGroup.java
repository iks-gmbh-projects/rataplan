package iks.surveytool.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class QuestionGroup extends AbstractEntity {

    private String title;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "question_group_id")
    private List<Question> questions;
}
