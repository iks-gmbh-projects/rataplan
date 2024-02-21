package de.iks.rataplan.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@IdClass(ContactId.class)
@Table(
    name = "contact",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"ownerId", "userId"}
    )
)
public class Contact implements OwnedEntity {
    @Id
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, optional = false)
    @JoinColumn(name = "ownerId")
    private User owner;
    
    @Id
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, optional = false)
    @JoinColumn(name = "userId")
    private User user;
    
    @ManyToMany(mappedBy = "contacts", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private Set<ContactGroup> groups;
}
