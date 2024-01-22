package de.iks.rataplan.service;

import de.iks.rataplan.domain.User;
import de.iks.rataplan.dto.AllContactsDTO;
import de.iks.rataplan.dto.ContactGroupDTO;

public interface ContactService {
    Integer addContact(User currentUser, Integer contact);
    AllContactsDTO getContacts(User currentUser);
    void deleteContact(User currentUser, int toDelete);
    ContactGroupDTO createGroup(User currentUser, ContactGroupDTO group);
    ContactGroupDTO renameGroup(User currentUser, long groupId, String newName);
    ContactGroupDTO addToGroup(User currentUser, long groupId, int contact);
    ContactGroupDTO removeFromGroup(User currentUser, long groupId, int contactId);
    void deleteGroup(User currentUser, long idToDelete);
}
