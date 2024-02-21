package de.iks.rataplan.repository;

import de.iks.rataplan.domain.Contact;
import de.iks.rataplan.domain.ContactId;
import de.iks.rataplan.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.stream.Stream;

public interface ContactRepository extends JpaRepository<Contact, ContactId> {
    Stream<Contact> findAllByOwner(User user);
    Stream<Contact> findAllByOwnerAndGroupsEmpty(User user);
}
