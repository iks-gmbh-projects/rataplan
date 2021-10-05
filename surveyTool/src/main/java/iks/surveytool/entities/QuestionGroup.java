package iks.surveytool.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class QuestionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String description;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @OneToMany(mappedBy = "questionGroup", cascade = CascadeType.ALL)
    private List<Question> questions;
}
