package de.iks.rataplan.repository;

import de.iks.rataplan.domain.Contact;
import de.iks.rataplan.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.stream.Stream;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    Optional<Contact> findByOwnerAndUser(User owner, User user);
    void deleteByOwnerAndUser(User owner, User user);
    Stream<Contact> findAllByOwner(User user);
    Stream<Contact> findAllByOwnerAndGroupsEmpty(User user);
}
