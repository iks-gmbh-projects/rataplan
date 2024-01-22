package de.iks.rataplan.service;

import de.iks.rataplan.domain.Contact;
import de.iks.rataplan.domain.ContactGroup;
import de.iks.rataplan.domain.ContactId;
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
    public Integer addContact(User currentUser, Integer userId) {
        Contact inserted = contactRepository.saveAndFlush(Contact.builder()
            .owner(currentUser)
            .user(userService.getUserFromId(userId))
            .build());
        return inserted.getUser().getId();
    }
    
    @Override
    @Transactional(readOnly = true)
    public AllContactsDTO getContacts(User currentUser) {
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
                .collect(Collectors.toUnmodifiableList())
        );
    }
    @Override
    @Transactional
    public void deleteContact(User currentUser, int toDelete) {
        contactRepository.deleteById(new ContactId(currentUser, userService.getUserFromId(toDelete)));
    }
    @Override
    @Transactional
    public ContactGroupDTO createGroup(User currentUser, ContactGroupDTO group) {
        ContactGroup.ContactGroupBuilder builder = ContactGroup.builder()
            .owner(currentUser)
            .name(cryptoService.encryptDB(group.getName()));
        return toDTO(contactGroupRepository.saveAndFlush(builder.build()));
    }
    private Set<Contact> toContactSet(User currentUser, Collection<Integer> collection) {
        if(collection == null) return null;
        return collection.stream()
            .filter(Objects::nonNull)
            .distinct()
            .map(userService::getUserFromId)
            .map(id -> new ContactId(currentUser, id))
            .map(contactRepository::findById)
            .flatMap(Optional::stream)
            .collect(Collectors.toUnmodifiableSet());
    }
    @Override
    @Transactional
    public ContactGroupDTO renameGroup(User currentUser, long groupId, String newName) {
        Optional<ContactGroup> entity = contactGroupRepository.findById(groupId).filter(currentUser::owns);
        entity.ifPresent(grp -> grp.setName(cryptoService.encryptDBRaw(newName)));
        return entity.map(contactGroupRepository::saveAndFlush).map(this::toDTO).orElse(null);
    }
    @Override
    @Transactional
    public ContactGroupDTO addToGroup(User currentUser, long groupId, int contactId) {
        return contactRepository.findById(new ContactId(currentUser, userService.getUserFromId(contactId)))
            .flatMap(ctc -> {
                Optional<ContactGroup> entity = contactGroupRepository.findById(groupId).filter(currentUser::owns);
                entity.ifPresent(grp -> grp.getContacts().add(ctc));
                return entity.map(contactGroupRepository::saveAndFlush).map(this::toDTO);
            })
            .orElse(null);
    }
    @Override
    @Transactional
    public ContactGroupDTO removeFromGroup(User currentUser, long groupId, int contactId) {
        Optional<ContactGroup> entity = contactGroupRepository.findById(groupId).filter(currentUser::owns);
        entity.ifPresent(grp -> grp.getContacts().removeIf(ctc -> Objects.equals(ctc.getUser().getId(), contactId)));
        return entity.map(contactGroupRepository::saveAndFlush).map(this::toDTO).orElse(null);
    }
    @Override
    public void deleteGroup(User currentUser, long idToDelete) {
        contactGroupRepository.findById(idToDelete).filter(currentUser::owns).ifPresent(contactGroupRepository::delete);
        contactGroupRepository.flush();
    }
}
