package de.iks.rataplan.controller;

import de.iks.rataplan.dto.AllContactsDTO;
import de.iks.rataplan.dto.ContactGroupDTO;
import de.iks.rataplan.exceptions.InvalidTokenException;
import de.iks.rataplan.exceptions.RataplanAuthException;
import de.iks.rataplan.service.ContactService;
import de.iks.rataplan.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static de.iks.rataplan.controller.RataplanAuthRestController.JWT_COOKIE_NAME;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/contacts")
@Slf4j
public class ContactsController {
    private final JwtTokenService jwtTokenService;
    private final ContactService contactService;
    private String validateTokenOrThrow(String cookieToken, String headerToken) throws RataplanAuthException {
        String token;
        if(headerToken == null) token = cookieToken;
        else if(cookieToken == null) token = headerToken;
        else if(cookieToken.equals(headerToken)) token = cookieToken;
        else throw new RataplanAuthException("Different tokens provided by cookie and header.");
        if(!jwtTokenService.isTokenValid(token)) {
            throw new InvalidTokenException("Invalid token");
        }
        return token;
    }
    @PostMapping
    public ResponseEntity<Integer> createContact(
        @RequestHeader(value = JWT_COOKIE_NAME, required = false) String tokenHeader,
        @CookieValue(value = JWT_COOKIE_NAME, required = false) String tokenCookie,
        @RequestBody int contact
    ) {
        String token = validateTokenOrThrow(tokenCookie, tokenHeader);
        try {
            contact = contactService.addContact(jwtTokenService.getUserIdFromToken(token), contact);
        } catch(DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(contact);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContact(
        @RequestHeader(value = JWT_COOKIE_NAME, required = false) String tokenHeader,
        @CookieValue(value = JWT_COOKIE_NAME, required = false) String tokenCookie,
        @PathVariable int id
    ) {
        String token = validateTokenOrThrow(tokenCookie, tokenHeader);
        contactService.deleteContact(jwtTokenService.getUserIdFromToken(token), id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/group")
    public ResponseEntity<ContactGroupDTO> createGroup(
        @RequestHeader(value = JWT_COOKIE_NAME, required = false) String tokenHeader,
        @CookieValue(value = JWT_COOKIE_NAME, required = false) String tokenCookie,
        @RequestBody ContactGroupDTO group
    ) {
        String token = validateTokenOrThrow(tokenCookie, tokenHeader);
        try {
            group = contactService.createGroup(jwtTokenService.getUserIdFromToken(token), group);
        } catch(DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(group);
    }
    
    @GetMapping("/group/{id}")
    public ResponseEntity<ContactGroupDTO> getGroup(
        @RequestHeader(value = JWT_COOKIE_NAME, required = false) String tokenHeader,
        @CookieValue(value = JWT_COOKIE_NAME, required = false) String tokenCookie,
        @PathVariable long id
    ) {
        String token = validateTokenOrThrow(tokenCookie, tokenHeader);
        ContactGroupDTO group = contactService.getGroup(jwtTokenService.getUserIdFromToken(token), id);
        if(group == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(group);
    }
    
    @PutMapping("/group/{id}/name")
    public ResponseEntity<ContactGroupDTO> renameGroup(
        @RequestHeader(value = JWT_COOKIE_NAME, required = false) String tokenHeader,
        @CookieValue(value = JWT_COOKIE_NAME, required = false) String tokenCookie,
        @PathVariable long id,
        @RequestBody String name
    ) {
        String token = validateTokenOrThrow(tokenCookie, tokenHeader);
        ContactGroupDTO group;
        try {
            group = contactService.renameGroup(jwtTokenService.getUserIdFromToken(token), id, name);
        } catch(DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if(group == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(group);
    }
    
    @PostMapping("/group/{id}/contact")
    public ResponseEntity<ContactGroupDTO> groupContact(
        @RequestHeader(value = JWT_COOKIE_NAME, required = false) String tokenHeader,
        @CookieValue(value = JWT_COOKIE_NAME, required = false) String tokenCookie,
        @PathVariable long id,
        @RequestBody int contactId
    ) {
        String token = validateTokenOrThrow(tokenCookie, tokenHeader);
        ContactGroupDTO group = contactService.addToGroup(jwtTokenService.getUserIdFromToken(token), id, contactId);
        if(group == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(group);
    }
    
    @DeleteMapping("/group/{id}/contact/{cid}")
    public ResponseEntity<ContactGroupDTO> ungroupContact(
        @RequestHeader(value = JWT_COOKIE_NAME, required = false) String tokenHeader,
        @CookieValue(value = JWT_COOKIE_NAME, required = false) String tokenCookie,
        @PathVariable long id,
        @PathVariable int cid
    ) {
        String token = validateTokenOrThrow(tokenCookie, tokenHeader);
        ContactGroupDTO group = contactService.removeFromGroup(jwtTokenService.getUserIdFromToken(token), id, cid);
        if(group == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(group);
    }
    
    @DeleteMapping("/group/{id}")
    public ResponseEntity<?> deleteGroup(
        @RequestHeader(value = JWT_COOKIE_NAME, required = false) String tokenHeader,
        @CookieValue(value = JWT_COOKIE_NAME, required = false) String tokenCookie,
        @PathVariable long id
    ) {
        String token = validateTokenOrThrow(tokenCookie, tokenHeader);
        contactService.deleteGroup(jwtTokenService.getUserIdFromToken(token), id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping
    public ResponseEntity<AllContactsDTO> getAll(
        @RequestHeader(value = JWT_COOKIE_NAME, required = false) String tokenHeader,
        @CookieValue(value = JWT_COOKIE_NAME, required = false) String tokenCookie
    ) {
        String token = validateTokenOrThrow(tokenCookie, tokenHeader);
        return ResponseEntity.ok(contactService.getContacts(jwtTokenService.getUserIdFromToken(token)));
    }
}
