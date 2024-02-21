package de.iks.rataplan.service;

import de.iks.rataplan.domain.User;
import de.iks.rataplan.dto.AllContactsDTO;
import de.iks.rataplan.dto.ContactGroupDTO;

public interface ContactService {
    Integer addContact(User currentUser, Integer contact);
    AllContactsDTO getContacts(User currentUser);
    void deleteContact(User currentUser, int toDelete);
    ContactGroupDTO createGroup(User currentUser, ContactGroupDTO group);
    ContactGroupDTO updateGroup(User currentUser, ContactGroupDTO group);
    void deleteGroup(User currentUser, long idToDelete);
}
