package de.iks.rataplan.domain;

public interface OwnedEntity {
    User getOwner();
    void setOwner(User owner);
}
