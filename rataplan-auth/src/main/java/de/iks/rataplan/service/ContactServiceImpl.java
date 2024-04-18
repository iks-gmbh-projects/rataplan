package de.iks.rataplan.service;

import de.iks.rataplan.domain.Contact;
import de.iks.rataplan.domain.ContactGroup;
import de.iks.rataplan.domain.User;
import de.iks.rataplan.dto.AllContactsDTO;
import de.iks.rataplan.dto.ContactGroupDTO;
import de.iks.rataplan.repository.ContactGroupRepository;
import de.iks.rataplan.repository.ContactRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {
    private final ContactRepository contactRepository;
    private final ContactGroupRepository contactGroupRepository;
    private final CryptoService cryptoService;
    private final UserService userService;
    
    @Override
    @Transactional
    public Integer addContact(int currentUserId, int userId) {
        final User currentUser = userService.getUserFromId(currentUserId);
        Contact inserted = contactRepository.saveAndFlush(Contact.builder()
            .owner(currentUser)
            .user(userService.getUserFromId(userId))
            .build());
        return inserted.getUser().getId();
    }
    
    @Override
    @Transactional(readOnly = true)
    public AllContactsDTO getContacts(int currentUserId) {
        final User currentUser = userService.getUserFromId(currentUserId);
        return new AllContactsDTO(
            contactGroupRepository.findAllByOwner(currentUser)
                .map(this::toDTO)
                .collect(Collectors.toUnmodifiableList()),
            contactRepository.findAllByOwnerAndGroupsEmpty(currentUser)
                .map(Contact::getUser)
                .map(User::getId)
                .collect(Collectors.toUnmodifiableList())
        );
    }
    
    private ContactGroupDTO toDTO(ContactGroup entity) {
        return new ContactGroupDTO(
            entity.getId(),
            cryptoService.decryptDBRaw(entity.getName()),
            entity.getContacts()
                .stream()
                .map(Contact::getUser)
                .map(User::getId)
                .sorted()
                .collect(Collectors.toUnmodifiableList())
        );
    }
    @Override
    @Transactional
    public void deleteContact(int currentUserId, int toDelete) {
        final User currentUser = userService.getUserFromId(currentUserId);
        contactRepository.deleteByOwnerAndUser(currentUser, userService.getUserFromId(toDelete));
        contactRepository.flush();
    }
    @Override
    @Transactional(readOnly = true)
    public ContactGroupDTO getGroup(int currentUserId, long groupId) {
        final User currentUser = userService.getUserFromId(currentUserId);
        return contactGroupRepository.findById(groupId)
            .filter(currentUser::owns)
            .map(this::toDTO)
            .orElse(null);
    }
    @Override
    @Transactional
    public ContactGroupDTO createGroup(int currentUserId, ContactGroupDTO group) {
        final User currentUser = userService.getUserFromId(currentUserId);
        ContactGroup.ContactGroupBuilder builder = ContactGroup.builder()
            .owner(currentUser)
            .name(cryptoService.encryptDB(group.getName()))
            .contacts(Set.of());
        return toDTO(contactGroupRepository.saveAndFlush(builder.build()));
    }
    @Override
    @Transactional
    public ContactGroupDTO renameGroup(int currentUserId, long groupId, String newName) {
        final User currentUser = userService.getUserFromId(currentUserId);
        Optional<ContactGroup> entity = contactGroupRepository.findById(groupId).filter(currentUser::owns);
        entity.ifPresent(grp -> grp.setName(cryptoService.encryptDBRaw(newName)));
        return entity.map(contactGroupRepository::saveAndFlush).map(this::toDTO).orElse(null);
    }
    @Override
    @Transactional
    public ContactGroupDTO addToGroup(int currentUserId, long groupId, int contactId) {
        final User currentUser = userService.getUserFromId(currentUserId);
        return contactRepository.findByOwnerAndUser(currentUser, userService.getUserFromId(contactId))
            .flatMap(ctc -> {
                Optional<ContactGroup> entity = contactGroupRepository.findById(groupId).filter(currentUser::owns);
                entity.ifPresent(grp -> grp.getContacts().add(ctc));
                return entity.map(contactGroupRepository::saveAndFlush).map(this::toDTO);
            })
            .orElse(null);
    }
    @Override
    @Transactional
    public ContactGroupDTO removeFromGroup(int currentUserId, long groupId, int contactId) {
        final User currentUser = userService.getUserFromId(currentUserId);
        Optional<ContactGroup> entity = contactGroupRepository.findById(groupId).filter(currentUser::owns);
        entity.ifPresent(grp -> grp.getContacts().removeIf(ctc -> Objects.equals(ctc.getUser().getId(), contactId)));
        return entity.map(contactGroupRepository::saveAndFlush).map(this::toDTO).orElse(null);
    }
    @Override
    public void deleteGroup(int currentUserId, long idToDelete) {
        final User currentUser = userService.getUserFromId(currentUserId);
        contactGroupRepository.findById(idToDelete).filter(currentUser::owns).ifPresent(contactGroupRepository::delete);
        contactGroupRepository.flush();
    }
}