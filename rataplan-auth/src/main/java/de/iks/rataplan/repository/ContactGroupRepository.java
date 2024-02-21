package de.iks.rataplan.repository;

import de.iks.rataplan.domain.ContactGroup;
import de.iks.rataplan.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.stream.Stream;

public interface ContactGroupRepository extends JpaRepository<ContactGroup, Long> {
    Stream<ContactGroup> findAllByOwner(User user);
}
