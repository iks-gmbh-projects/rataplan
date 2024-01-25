package de.iks.rataplan.service;

import de.iks.rataplan.domain.Contact;
import de.iks.rataplan.dto.AllContactsDTO;
import de.iks.rataplan.dto.ContactGroupDTO;
import de.iks.rataplan.repository.ContactRepository;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static de.iks.rataplan.testutils.TestConstants.FILE_INITIAL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {
    ContactServiceImpl.class,
    UserServiceImpl.class,
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class,
    FlywayAutoConfiguration.class
})
@EntityScan(basePackageClasses = Contact.class)
@EnableJpaRepositories(basePackageClasses = ContactRepository.class)
@EnableTransactionManagement
@TestExecutionListeners(
    value = TransactionDbUnitTestExecutionListener.class,
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@DatabaseSetup(ContactServiceTest.CONTACT_FILE_INITIAL)
public class ContactServiceTest {
    private static final String BASE_LINK = "classpath:test/db/service/contact";
    static final String CONTACT_FILE_INITIAL = BASE_LINK + FILE_INITIAL;
    
    @MockBean
    private CryptoServiceImpl cryptoService;
    @MockBean
    private JwtTokenService jts;
    @MockBean
    private BCryptPasswordEncoder pwenc;
    @MockBean
    private SurveyToolMessageService stms;
    @MockBean
    private BackendMessageService bms;
    @Autowired
    private ContactService contactService;
    @Autowired
    private UserService userService;
    
    @BeforeEach
    void setup() {
        lenient().when(cryptoService.encryptDBRaw(anyString()))
            .then(a -> a.getArgument(0, String.class).getBytes(StandardCharsets.UTF_8));
        lenient().when(cryptoService.decryptDBRaw(any(byte[].class)))
            .then(a -> new String(a.getArgument(0), StandardCharsets.UTF_8));
        lenient().when(cryptoService.decryptDB(any(byte[].class))).thenCallRealMethod();
        lenient().when(cryptoService.encryptDB(anyString())).thenCallRealMethod();
    }
    
    @Test
    void addContactTest() {
        Integer added = contactService.addContact(1, 2);
        assertEquals(2, added);
        AllContactsDTO allContacts = contactService.getContacts(1);
        assertNotNull(allContacts);
        assertNotNull(allContacts.getUngrouped());
        assertTrue(allContacts.getUngrouped().contains(2));
    }
    
    @Test
    @ExpectedDatabase(value = CONTACT_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    void addExistingContactTest() {
        assertThrows(
            DataIntegrityViolationException.class,
            () -> contactService.addContact(1, 1)
        );
    }
    
    @Test
    @ExpectedDatabase(value = CONTACT_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    void addNonExistingContactTest() {
        assertThrows(
            NoSuchElementException.class,
            () -> contactService.addContact(1, 9999)
        );
    }
    
    @Test
    @ExpectedDatabase(value = CONTACT_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    void getContactsTest() {
        AllContactsDTO allContacts = contactService.getContacts(1);
        assertNotNull(allContacts);
        assertIterableEquals(List.of(3), allContacts.getUngrouped());
        assertEquals(1, allContacts.getGroups().size());
        assertIterableEquals(List.of(
            new ContactGroupDTO(
                1L,
                "oldGroup",
                List.of(1)
            )
        ), allContacts.getGroups());
    }
    
    @Test
    void deleteContactTest() {
        contactService.deleteContact(1, 1);
        
        AllContactsDTO allContacts = contactService.getContacts(1);
        assertNotNull(allContacts);
        assertIterableEquals(List.of(
            new ContactGroupDTO(
                1L,
                "oldGroup",
                List.of()
            )
        ),  allContacts.getGroups());
        assertIterableEquals(List.of(3), allContacts.getUngrouped());
        
        AllContactsDTO allContacts2 = contactService.getContacts(2);
        assertNotNull(allContacts);
        assertIterableEquals(List.of(new ContactGroupDTO(
            2L,
            "oldGroup",
            List.of()
        )),  allContacts2.getGroups());
        assertIterableEquals(List.of(1), allContacts2.getUngrouped());
    }
    
    @Test
    void createGroupTest() {
        ContactGroupDTO created = contactService.createGroup(1, new ContactGroupDTO(null, "testGroup", null));
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("testGroup", created.getName());
        assertTrue(created.getContacts().isEmpty());
        assertEquals(created, contactService.getGroup(1, created.getId()));
    }
    
    @Test
    void createDuplicateGroupTest() {
        assertThrows(
            DataIntegrityViolationException.class,
            () -> contactService.createGroup(1, new ContactGroupDTO(null, "oldGroup", null))
        );
    }
    
    @Test
    void renameGroupTest() {
        ContactGroupDTO edited = contactService.renameGroup(1, 1, "newGroup");
        assertNotNull(edited);
        assertEquals(1, edited.getId());
        assertEquals("newGroup", edited.getName());
        assertEquals(1, edited.getContacts().size());
        assertEquals(1, edited.getContacts().get(0));
        assertEquals(edited, contactService.getGroup(1, 1));
    }
    
    @Test
    void addToGroupTest() {
        ContactGroupDTO edited = contactService.addToGroup(1, 1, 3);
        assertNotNull(edited);
        assertEquals(1, edited.getId());
        assertEquals("oldGroup", edited.getName());
        assertNotNull(edited.getContacts());
        assertEquals(Set.of(1, 3), new HashSet<>(edited.getContacts()));
        
        assertEquals(edited, contactService.getGroup(1, 1));
        assertTrue(contactService.getContacts(1).getUngrouped().isEmpty());
    }
    
    @Test
    void removeFromGroupTest() {
        ContactGroupDTO edited = contactService.removeFromGroup(1, 1,1);
        assertNotNull(edited);
        assertEquals(1, edited.getId());
        assertEquals("oldGroup", edited.getName());
        assertTrue(edited.getContacts().isEmpty());
        assertEquals(edited, contactService.getGroup(1, 1));
        assertEquals(2, contactService.getContacts(1).getUngrouped().size());
    }
    
    @Test
    void deleteGroupTest() {
        contactService.deleteGroup(1, 1);
        
        AllContactsDTO allContacts = contactService.getContacts(1);
        assertNotNull(allContacts);
        assertIterableEquals(List.of(), allContacts.getGroups());
        assertEquals(2, allContacts.getUngrouped().size());
    }
    
    @Test
    void deleteBadGroupTest() {
        contactService.deleteGroup(1, 2);
        assertNotNull(contactService.getGroup(1, 1));
        assertNotNull(contactService.getGroup(2, 2));
    }
}
