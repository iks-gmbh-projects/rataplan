package de.iks.rataplan.service;

import de.iks.rataplan.domain.User;
import de.iks.rataplan.domain.notifications.EmailCycle;
import de.iks.rataplan.domain.notifications.NotificationMailData;
import de.iks.rataplan.dto.NotificationDTO;
import de.iks.rataplan.dto.NotificationSettingsDTO;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import de.iks.rataplan.repository.NotificationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static de.iks.rataplan.testutils.TestConstants.FILE_INITIAL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(
    classes = {
        NotificationServiceImpl.class,
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        FlywayAutoConfiguration.class
    }
)
@EntityScan(basePackageClasses = User.class)
@EnableJpaRepositories(basePackageClasses = NotificationRepository.class)
@EnableTransactionManagement
@TestExecutionListeners(
    value = TransactionDbUnitTestExecutionListener.class,
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@DatabaseSetup(NotificationServiceTest.NOTIFICATION_FILE_INITIAL)
public class NotificationServiceTest {
    private static final String BASE_LINK = "classpath:test/db/service/notification";
    static final String NOTIFICATION_FILE_INITIAL = BASE_LINK + FILE_INITIAL;
    @MockBean
    private MailService mailService;
    @MockBean
    private CryptoService cryptoService;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationService notificationService;
    
    private TransactionTemplate tt;
    
    @BeforeEach
    void setup() {
        tt = new TransactionTemplate(transactionManager);
        lenient().when(cryptoService.decryptDBRaw(any()))
            .then(a -> new String(a.getArgument(0), StandardCharsets.UTF_8));
        lenient().when(cryptoService.decryptDB(any())).thenCallRealMethod();
        lenient().when(cryptoService.encryptDBRaw(anyString()))
            .then(a -> a.getArgument(0, String.class).getBytes(StandardCharsets.UTF_8));
        lenient().when(cryptoService.encryptDB(anyString())).thenCallRealMethod();
    }
    
    @Test
    void testGetNotificationSettings() {
        assertEquals(
            new NotificationSettingsDTO(EmailCycle.INSTANT,
                Map.ofEntries(Map.entry("general", EmailCycle.SUPPRESS), Map.entry("test", EmailCycle.DAILY_DIGEST))
            ),
            notificationService.getNotificationSettings(1)
        );
    }
    
    @Test
    @ExpectedDatabase(value = NOTIFICATION_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    void testGetNotificationSettingsBadUserId() {
        assertNull(notificationService.getNotificationSettings(2));
    }
    
    @Test
    void testUpdateNotificationSettings() {
        Map<String, EmailCycle> settings = Map.ofEntries(Map.entry("general", EmailCycle.INSTANT),
            Map.entry("test", EmailCycle.WEEKLY_DIGEST)
        );
        NotificationSettingsDTO response = notificationService.updateNotificationSettings(
            1,
            new NotificationSettingsDTO(EmailCycle.SUPPRESS, settings)
        );
        assertNotNull(response);
        assertEquals(EmailCycle.SUPPRESS, response.getDefaultSettings());
        assertEquals(settings, response.getCategorySettings());
        
        assertEquals(response, notificationService.getNotificationSettings(1));
        
        tt.executeWithoutResult(s -> {
            assertTrue(notificationRepository.findCycleNotifications(EmailCycle.SUPPRESS).findAny().isEmpty());
            assertTrue(notificationRepository.findCycleNotifications(EmailCycle.INSTANT).findAny().isEmpty());
        });
    }
    @Test
    @ExpectedDatabase(value = NOTIFICATION_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    void testNotifySuppressed() {
        notificationService.notify(List.of(new NotificationDTO(
            1,
            null,
            "general",
            "suppressed",
            "suppressedContent",
            "suppressedSContent"
        )));
        verifyNoInteractions(mailService);
    }
    @Test
    @ExpectedDatabase(value = NOTIFICATION_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    void testNotifyInstant() {
        notificationService.notify(List.of(new NotificationDTO(
            1,
            null,
            "basic",
            "instant",
            "instantContent",
            "instantSContent"
        )));
        verify(mailService).sendNotification("peter@sch.mitz", new NotificationMailData("instant", "instantContent"));
        verifyNoMoreInteractions(mailService);
    }
    @Test
    void sendSummary() {
        notificationService.sendSummary(Set.of(EmailCycle.DAILY_DIGEST));
        verify(mailService).sendNotificationSummary("peter@sch.mitz", List.of(
            new NotificationMailData("TestNotification", "TestNotificationContent")
        ));
        verifyNoMoreInteractions(mailService);
    }
    
    @Test
    void sendSummaryOnce() {
        notificationService.sendSummary(Set.of(EmailCycle.DAILY_DIGEST));
        notificationService.sendSummary(Set.of(EmailCycle.DAILY_DIGEST));
        verify(mailService).sendNotificationSummary("peter@sch.mitz", List.of(
            new NotificationMailData("TestNotification", "TestNotificationContent")
        ));
        verifyNoMoreInteractions(mailService);
    }
    
    @Test
    void testNotifyCycle() {
        notificationService.notify(List.of(new NotificationDTO(
            1,
            null,
            "test",
            "cycle",
            "cycleContent",
            "cycleSContent"
        )));
        verifyNoInteractions(mailService);
        notificationService.sendSummary(Set.of(EmailCycle.DAILY_DIGEST));
        verify(mailService).sendNotificationSummary("peter@sch.mitz", List.of(
            new NotificationMailData("TestNotification", "TestNotificationContent"),
            new NotificationMailData("cycle", "cycleSContent")
        ));
        verifyNoMoreInteractions(mailService);
    }
}
