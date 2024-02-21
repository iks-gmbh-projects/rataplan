package de.iks.rataplan.service;

import de.iks.rataplan.dto.AllContactsDTO;
import de.iks.rataplan.dto.ContactGroupDTO;

public interface ContactService {
    Integer addContact(int currentUser, int contact);
    AllContactsDTO getContacts(int currentUser);
    void deleteContact(int currentUser, int toDelete);
    ContactGroupDTO createGroup(int currentUser, ContactGroupDTO group);
    ContactGroupDTO getGroup(int currentUser, long groupId);
    ContactGroupDTO renameGroup(int currentUser, long groupId, String newName);
    ContactGroupDTO addToGroup(int currentUser, long groupId, int contact);
    ContactGroupDTO removeFromGroup(int currentUser, long groupId, int contactId);
    void deleteGroup(int currentUser, long idToDelete);
}
