package de.iks.rataplan.controller;

import de.iks.rataplan.dto.AllContactsDTO;
import de.iks.rataplan.dto.ContactGroupDTO;
import de.iks.rataplan.service.CryptoServiceImpl;
import de.iks.rataplan.service.JwtTokenService;
import de.iks.rataplan.service.RataplanUserDetails;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static de.iks.rataplan.testutils.ITConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

@ActiveProfiles("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestExecutionListeners(
    listeners = {
        DirtiesContextTestExecutionListener.class, TransactionDbUnitTestExecutionListener.class
    }, mergeMode = MERGE_WITH_DEFAULTS
)
public class ContactsControllerIT {
    private static final String BASE_LINK = "classpath:integration/db/controller";
    private static final String CONTACT_FILE_INITIAL = BASE_LINK + FILE_INITIAL;
    private static final String CONTACTS_ID = CONTACTS + "/{id}";
    private static final String GROUP = CONTACTS + "/group";
    private static final String GROUP_ID = GROUP + "/{id}";
    private static final String GROUP_NAME = GROUP_ID + "/name";
    private static final String GROUP_CONTACT = GROUP_ID + "/contact";
    private static final String GROUP_CONTACT_ID = GROUP_CONTACT + "/{cid}";
    
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private JwtTokenService jwtTokenService;
    
    @MockBean
    private CryptoServiceImpl cryptoService;
    
    private final HttpHeaders headers = new HttpHeaders();
    
    @BeforeEach
    void setup() {
        lenient().when(cryptoService.decryptDBRaw(any()))
            .then(a -> new String(a.getArgument(0), StandardCharsets.UTF_8));
        lenient().when(cryptoService.encryptDBRaw(anyString()))
            .then(a -> a.getArgument(0, String.class).getBytes(StandardCharsets.UTF_8));
        lenient().when(cryptoService.decryptDB(any())).thenCallRealMethod();
        lenient().when(cryptoService.encryptDB(anyString())).thenCallRealMethod();
        String token = jwtTokenService.generateLoginToken(new RataplanUserDetails(1, "peter", "peter@sch.mitz", null, true));
        headers.setBearerAuth(token);
    }
    
    @Test
    @DatabaseSetup(CONTACT_FILE_INITIAL)
    void addContact() {
        ResponseEntity<Integer> response = restTemplate.exchange(CONTACTS,
            HttpMethod.POST,
            new HttpEntity<>(2, headers),
            Integer.class
        );
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.hasBody());
        assertEquals(2, response.getBody());
        
        //TODO check new state
    }
    
    @Test
    @DatabaseSetup(CONTACT_FILE_INITIAL)
    @ExpectedDatabase(value = CONTACT_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    void addExistingContact() {
        ResponseEntity<Integer> response = restTemplate.exchange(CONTACTS,
            HttpMethod.POST,
            new HttpEntity<>(3, headers),
            Integer.class
        );
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertFalse(response.hasBody());
    }
    
    @Test
    @DatabaseSetup(CONTACT_FILE_INITIAL)
    void deleteContact() {
        ResponseEntity<?> response = restTemplate.exchange(CONTACTS_ID,
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            void.class,
            1
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.hasBody());
        
        //TODO check new state
    }
    
    @Test
    @DatabaseSetup(CONTACT_FILE_INITIAL)
    @ExpectedDatabase(value = CONTACT_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    void deleteContactAcrossUser() {
        ResponseEntity<?> response = restTemplate.exchange(CONTACTS_ID,
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            void.class,
            2
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.hasBody());
    }
    
    @Test
    @DatabaseSetup(CONTACT_FILE_INITIAL)
    void addGroup() {
        ResponseEntity<ContactGroupDTO> response = restTemplate.exchange(GROUP,
            HttpMethod.POST,
            new HttpEntity<>(new ContactGroupDTO(null, "newGroup", null), headers),
            ContactGroupDTO.class
        );
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.hasBody());
        ContactGroupDTO body = response.getBody();
        assertNotNull(body);
        assertNotNull(body.getId());
        assertEquals("newGroup", body.getName());
        assertNotNull(body.getContacts());
        assertTrue(body.getContacts().isEmpty());
        
        ResponseEntity<ContactGroupDTO> response2 = restTemplate.exchange(GROUP_ID,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            ContactGroupDTO.class,
            body.getId()
        );
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertTrue(response2.hasBody());
        assertEquals(body, response2.getBody());
    }
    
    @Test
    @DatabaseSetup(CONTACT_FILE_INITIAL)
    @ExpectedDatabase(value = CONTACT_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    void addExistingGroup() {
        ResponseEntity<ContactGroupDTO> response = restTemplate.exchange(GROUP,
            HttpMethod.POST,
            new HttpEntity<>(new ContactGroupDTO(null, "oldGroup", null), headers),
            ContactGroupDTO.class
        );
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertFalse(response.hasBody());
    }
    
    @Test
    @DatabaseSetup(CONTACT_FILE_INITIAL)
    @ExpectedDatabase(value = CONTACT_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    void getGroup() {
        ResponseEntity<ContactGroupDTO> response = restTemplate.exchange(GROUP_ID,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            ContactGroupDTO.class,
            1
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.hasBody());
        assertEquals(new ContactGroupDTO(1L, "oldGroup", List.of(1)), response.getBody());
    }
    @Test
    @DatabaseSetup(CONTACT_FILE_INITIAL)
    @ExpectedDatabase(value = CONTACT_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    void getGroupAcrossUser() {
        ResponseEntity<ContactGroupDTO> response = restTemplate.exchange(GROUP_ID,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            ContactGroupDTO.class,
            2
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.hasBody());
    }
    
    @Test
    @DatabaseSetup(CONTACT_FILE_INITIAL)
    void renameGroup() {
        ResponseEntity<ContactGroupDTO> response = restTemplate.exchange(GROUP_NAME,
            HttpMethod.PUT,
            new HttpEntity<>("newGroup", headers),
            ContactGroupDTO.class,
            1
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.hasBody());
        ContactGroupDTO body = response.getBody();
        assertEquals(new ContactGroupDTO(1L, "newGroup", List.of(1)), body);
        
        ResponseEntity<ContactGroupDTO> response2 = restTemplate.exchange(GROUP_ID,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            ContactGroupDTO.class,
            1
        );
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertTrue(response2.hasBody());
        assertEquals(body, response2.getBody());
    }
    
    @Test
    @DatabaseSetup(CONTACT_FILE_INITIAL)
    @ExpectedDatabase(value = CONTACT_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    void renameGroupConflict() {
        ResponseEntity<ContactGroupDTO> response = restTemplate.exchange(GROUP_NAME,
            HttpMethod.PUT,
            new HttpEntity<>("oldGroup", headers),
            ContactGroupDTO.class,
            3
        );
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertFalse(response.hasBody());
    }
    
    @Test
    @DatabaseSetup(CONTACT_FILE_INITIAL)
    @ExpectedDatabase(value = CONTACT_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    void renameGroupAcrossUser() {
        ResponseEntity<ContactGroupDTO> response = restTemplate.exchange(GROUP_NAME,
            HttpMethod.PUT,
            new HttpEntity<>("oldGroup", headers),
            ContactGroupDTO.class,
            2
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.hasBody());
    }
    
    @Test
    @DatabaseSetup(CONTACT_FILE_INITIAL)
    @ExpectedDatabase(value = CONTACT_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    void getGroups() {
        ResponseEntity<AllContactsDTO> response = restTemplate.exchange(CONTACTS,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            AllContactsDTO.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.hasBody());
        AllContactsDTO compare = new AllContactsDTO(
            List.of(
                new ContactGroupDTO(1L, "oldGroup", List.of(1)),
                new ContactGroupDTO(3L, "emptyGroup", List.of())
            ),
            List.of(3)
        );
        assertEquals(compare, response.getBody());
    }
    
    @Test
    @DatabaseSetup(CONTACT_FILE_INITIAL)
    void groupContact() {
        ResponseEntity<ContactGroupDTO> response = restTemplate.exchange(GROUP_CONTACT,
            HttpMethod.POST,
            new HttpEntity<>(3, headers),
            ContactGroupDTO.class,
            3
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.hasBody());
        ContactGroupDTO body = response.getBody();
        assertEquals(new ContactGroupDTO(
            3L, "emptyGroup", List.of(3)
        ), body);
        
        ResponseEntity<ContactGroupDTO> response2 = restTemplate.exchange(GROUP_ID,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            ContactGroupDTO.class,
            3
        );
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertTrue(response2.hasBody());
        assertEquals(body, response2.getBody());
        
        ResponseEntity<AllContactsDTO> response3 = restTemplate.exchange(CONTACTS,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            AllContactsDTO.class
        );
        assertEquals(HttpStatus.OK, response3.getStatusCode());
        assertTrue(response3.hasBody());
        AllContactsDTO compare = new AllContactsDTO(
            List.of(
                new ContactGroupDTO(
                    1L,
                    "oldGroup",
                    List.of(1)
                ),
                body
            ),
            List.of()
        );
        assertEquals(compare, response3.getBody());
    }
    
    @Test
    @DatabaseSetup(CONTACT_FILE_INITIAL)
    void ungroupContact() {
        ResponseEntity<ContactGroupDTO> response = restTemplate.exchange(GROUP_CONTACT_ID,
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            ContactGroupDTO.class,
            1,
            1
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.hasBody());
        ContactGroupDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.getId());
        assertEquals("oldGroup", body.getName());
        assertNotNull(body.getContacts());
        assertTrue(body.getContacts().isEmpty());
        
        ResponseEntity<ContactGroupDTO> response2 = restTemplate.exchange(GROUP_ID,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            ContactGroupDTO.class,
            1
        );
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertTrue(response2.hasBody());
        assertEquals(body, response2.getBody());
        
        ResponseEntity<AllContactsDTO> response3 = restTemplate.exchange(CONTACTS,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            AllContactsDTO.class
        );
        assertEquals(HttpStatus.OK, response3.getStatusCode());
        assertTrue(response3.hasBody());
        AllContactsDTO compare = new AllContactsDTO(List.of(body, new ContactGroupDTO(3L, "emptyGroup", List.of())),
            List.of(1, 3)
        );
        assertEquals(compare, response3.getBody());
    }
    
    @Test
    @DatabaseSetup(CONTACT_FILE_INITIAL)
    void deleteGroup() {
        ResponseEntity<?> response = restTemplate.exchange(GROUP_ID,
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            void.class,
            1
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.hasBody());
        
        ResponseEntity<AllContactsDTO> response2 = restTemplate.exchange(CONTACTS,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            AllContactsDTO.class
        );
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertTrue(response2.hasBody());
        AllContactsDTO compare = new AllContactsDTO(List.of(new ContactGroupDTO(3L, "emptyGroup", List.of())),
            List.of(1, 3)
        );
        assertEquals(compare, response2.getBody());
    }
    
    @Test
    @DatabaseSetup(CONTACT_FILE_INITIAL)
    @ExpectedDatabase(value = CONTACT_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    void deleteGroupAcrossUser() {
        ResponseEntity<?> response = restTemplate.exchange(GROUP_ID,
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            void.class,
            2
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.hasBody());
    }
}