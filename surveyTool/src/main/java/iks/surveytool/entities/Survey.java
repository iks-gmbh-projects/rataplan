package iks.surveytool.entities;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Survey {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name;
    private String description;

    @Column(name = "startDate")
    @NotNull
    private LocalDateTime startDate;
    @Column(name = "endDate")
    @NotNull
    private LocalDateTime endDate;

    // Whether the survey is open to be answered or not:
    private boolean open;
    // ID for creator of survey to view results. May get changed to UUID:
    private String accessID;
    // Link for users to submit their answers:
    private String link;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL)
    private List<QuestionGroup> questionGroups;
}
