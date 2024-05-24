package de.iks.rataplan.controller;

import de.iks.rataplan.dto.AllContactsDTO;
import de.iks.rataplan.dto.ContactGroupDTO;
import de.iks.rataplan.service.ContactService;
import de.iks.rataplan.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/contacts")
@Slf4j
public class ContactsController {
    private final JwtTokenService jwtTokenService;
    private final ContactService contactService;
    
    @PostMapping
    public ResponseEntity<Integer> createContact(
        @AuthenticationPrincipal Jwt token,
        @RequestBody int contact
    ) {
        try {
            contact = contactService.addContact(jwtTokenService.getUserId(token), contact);
        } catch(DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(contact);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContact(
        @AuthenticationPrincipal Jwt token,
        @PathVariable int id
    ) {
        contactService.deleteContact(jwtTokenService.getUserId(token), id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/group")
    public ResponseEntity<ContactGroupDTO> createGroup(
        @AuthenticationPrincipal Jwt token,
        @RequestBody ContactGroupDTO group
    ) {
        try {
            group = contactService.createGroup(jwtTokenService.getUserId(token), group);
        } catch(DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(group);
    }
    
    @GetMapping("/group/{id}")
    public ResponseEntity<ContactGroupDTO> getGroup(
        @AuthenticationPrincipal Jwt token,
        @PathVariable long id
    ) {
        ContactGroupDTO group = contactService.getGroup(jwtTokenService.getUserId(token), id);
        if(group == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(group);
    }
    
    @PutMapping("/group/{id}/name")
    public ResponseEntity<ContactGroupDTO> renameGroup(
        @AuthenticationPrincipal Jwt token,
        @PathVariable long id,
        @RequestBody String name
    ) {
        ContactGroupDTO group;
        try {
            group = contactService.renameGroup(jwtTokenService.getUserId(token), id, name);
        } catch(DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if(group == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(group);
    }
    
    @PostMapping("/group/{id}/contact")
    public ResponseEntity<ContactGroupDTO> groupContact(
        @AuthenticationPrincipal Jwt token,
        @PathVariable long id,
        @RequestBody int contactId
    ) {
        ContactGroupDTO group = contactService.addToGroup(jwtTokenService.getUserId(token), id, contactId);
        if(group == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(group);
    }
    
    @DeleteMapping("/group/{id}/contact/{cid}")
    public ResponseEntity<ContactGroupDTO> ungroupContact(
        @AuthenticationPrincipal Jwt token,
        @PathVariable long id,
        @PathVariable int cid
    ) {
        ContactGroupDTO group = contactService.removeFromGroup(jwtTokenService.getUserId(token), id, cid);
        if(group == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(group);
    }
    
    @DeleteMapping("/group/{id}")
    public ResponseEntity<?> deleteGroup(
        @AuthenticationPrincipal Jwt token,
        @PathVariable long id
    ) {
        contactService.deleteGroup(jwtTokenService.getUserId(token), id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping
    public ResponseEntity<AllContactsDTO> getAll(
        @AuthenticationPrincipal Jwt token
    ) {
        return ResponseEntity.ok(contactService.getContacts(jwtTokenService.getUserId(token)));
    }
}