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
@Table(
    name = "contactGroup",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"ownerId", "name"}
    )
)
public class ContactGroup implements OwnedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, optional = false)
    @JoinColumn(name = "ownerId", nullable = false)
    private User owner;
    
    @Column(name = "name", nullable = false)
    private byte[] name;
    
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(
        name = "contacts2groups",
        joinColumns = {
            @JoinColumn(name = "groupOwnerId", referencedColumnName = "ownerId"),
            @JoinColumn(name = "groupId", referencedColumnName = "id")
        },
        inverseJoinColumns = {
            @JoinColumn(name = "contactOwnerId", referencedColumnName = "ownerId"),
            @JoinColumn(name = "contactId", referencedColumnName = "id")
        }
    )
    private Set<Contact> contacts;
}
