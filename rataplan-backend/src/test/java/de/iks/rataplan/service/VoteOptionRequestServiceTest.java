package de.iks.rataplan.service;

import de.iks.rataplan.domain.*;
import de.iks.rataplan.dto.restservice.NotificationType;
import de.iks.rataplan.exceptions.MalformedException;
import de.iks.rataplan.exceptions.ResourceNotFoundException;
import de.iks.rataplan.restservice.AuthService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static de.iks.rataplan.testutils.TestConstants.*;
import static de.iks.rataplan.utils.VoteBuilder.voteOptionList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestExecutionListeners(
    value = {DbUnitTestExecutionListener.class, TransactionalTestExecutionListener.class},
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@Transactional
public class VoteOptionRequestServiceTest {
    
    private static final String FILE_PATH = PATH + SERVICE + VOTES;
    
    @MockBean
    private CryptoService cryptoService;
    @MockBean
    private AuthService authService;
    
    @Autowired
    private VoteService voteService;
    
    @BeforeEach
    public void setup() throws NoSuchAlgorithmException {
        lenient().when(cryptoService.decryptDBRaw(any(byte[].class)))
            .thenAnswer(invocation -> new String(invocation.getArgument(0, byte[].class), StandardCharsets.UTF_8));
        lenient().when(cryptoService.decryptDB(anyString()))
            .thenAnswer(invocation -> invocation.getArgument(0, String.class));
        lenient().when(cryptoService.encryptDBRaw(anyString()))
            .thenAnswer(invocation -> invocation.getArgument(0, String.class).getBytes(StandardCharsets.UTF_8));
        lenient().when(cryptoService.encryptDB(anyString()))
            .thenAnswer(invocation -> invocation.getArgument(0, String.class));
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        KeyPair kp = kpg.generateKeyPair();
        lenient().when(cryptoService.getPrivateKey()).thenReturn(kp.getPrivate());
        lenient().when(cryptoService.getPublicKey()).thenReturn(kp.getPublic());
        
        lenient().doCallRealMethod()
            .when(authService)
            .sendNotification(anyString(), any(NotificationType.class), anyString(), anyString());
        lenient().doCallRealMethod()
            .when(authService)
            .sendNotification(anyString(), any(NotificationType.class), anyString(), anyString(), anyString());
        lenient().doCallRealMethod()
            .when(authService)
            .sendNotifications(any(String[].class), any(NotificationType.class), anyString(), anyString());
        lenient().doCallRealMethod()
            .when(authService)
            .sendNotifications(any(String[].class), any(NotificationType.class), anyString(), anyString(), anyString());
        lenient().doCallRealMethod()
            .when(authService)
            .sendNotification(anyInt(), any(NotificationType.class), anyString(), anyString());
        lenient().doCallRealMethod()
            .when(authService)
            .sendNotification(anyInt(), any(NotificationType.class), anyString(), anyString(), anyString());
        lenient().doCallRealMethod()
            .when(authService)
            .sendNotifications(any(int[].class), any(NotificationType.class), anyString(), anyString());
        lenient().doCallRealMethod()
            .when(authService)
            .sendNotifications(any(int[].class), any(NotificationType.class), anyString(), anyString(), anyString());
    }
    
    @Test
    @DatabaseSetup(FILE_EMPTY_DB)
    @ExpectedDatabase(value = FILE_PATH + CREATE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void createVote() {
        Vote vote = createSimpleVote();
        
        voteService.createVote(vote);
        verify(authService).sendMailNotifications(argThat(c -> c.size() == 1 && c.contains(IKS_MAIL)),
            eq(NotificationType.CREATE),
            anyString(),
            anyString(),
            anyString()
        );
        verify(authService, atLeast(0)).sendNotification(
            anyString(),
            any(NotificationType.class),
            anyString(),
            anyString()
        );
        verify(authService, atLeast(0)).sendNotification(
            anyString(),
            any(NotificationType.class),
            anyString(),
            anyString(),
            anyString()
        );
        verify(authService, atLeast(0)).sendMailNotifications(
            anyCollection(),
            any(NotificationType.class),
            anyString(),
            anyString()
        );
        verify(authService, atLeast(0)).sendMailNotifications(
            anyCollection(),
            any(NotificationType.class),
            anyString(),
            anyString(),
            anyString()
        );
        verify(authService, atLeast(0)).sendUserNotifications(
            anyCollection(),
            any(NotificationType.class),
            anyString(),
            anyString()
        );
        verify(authService, atLeast(0)).sendUserNotifications(
            anyCollection(),
            any(NotificationType.class),
            anyString(),
            anyString(),
            anyString()
        );
        verifyNoMoreInteractions(authService);
    }
    
    @Test
    @DatabaseSetup(FILE_EMPTY_DB)
    public void createVoteShouldFailHasNoOptions() {
        Vote vote = createSimpleVote();
        vote.setOptions(new ArrayList<>());
        assertThrows(MalformedException.class, () -> voteService.createVote(vote));
        verifyNoInteractions(authService);
    }
    
    @Test
    @DatabaseSetup(FILE_EMPTY_DB)
    public void createVoteShouldFailHasMember() {
        Vote vote = createSimpleVote();
        
        List<VoteParticipant> voteParticipants = vote.getParticipants();
        
        VoteParticipant voteParticipant = new VoteParticipant();
        voteParticipant.setName(new EncryptedString("Fritz macht den Fehler", false));
        voteParticipants.add(voteParticipant);
        
        vote.setParticipants(voteParticipants);
        
        assertThrows(MalformedException.class, () -> voteService.createVote(vote));
        verifyNoInteractions(authService);
    }
    
    @Test
    @DatabaseSetup(FILE_EMPTY_DB)
    public void createVoteShouldFailWrongVoteOptionConfig() {
        Vote vote = createSimpleVote();
        
        VoteOption voteOption = new VoteOption(new EncryptedString("iks Hilden", false), vote);
        voteOption.setUrl(new EncryptedString("thiswontwork.com", false));
        
        vote.setOptions(voteOptionList(voteOption, new VoteOption(new EncryptedString("homeoffice", false), vote)));
        
        assertThrows(MalformedException.class, () -> voteService.createVote(vote));
        verifyNoInteractions(authService);
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + GET + FILE_INITIAL)
    public void getAllVotes() {
        List<Vote> votes = voteService.getVotes();
        assertEquals(4, votes.size());
        verifyNoInteractions(authService);
    }
    
    @Test
    @DatabaseSetup(FILE_EMPTY_DB)
    public void getVotesNoneAvailable() {
        List<Vote> votes = voteService.getVotes();
        assertEquals(0, votes.size());
        verifyNoInteractions(authService);
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + GET + FILE_INITIAL)
    public void getVoteById() {
        Vote vote = voteService.getVoteById(1);
        assertEquals(vote.getTitle().getString(), "Coding Dojo");
        assertEquals(vote.getOptions().size(), 2);
        assertEquals(vote.getParticipants().size(), 0);
        verifyNoInteractions(authService);
    }
    
    @Test
    @DatabaseSetup(FILE_EMPTY_DB)
    public void getVoteByIdShouldFailDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> voteService.getVoteById(1));
        verifyNoInteractions(authService);
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + GET + FILE_INITIAL)
    public void getVotesByUser() {
        List<Vote> votes = voteService.getVotesForUser(1);
        assertEquals(2, votes.size());
        verifyNoInteractions(authService);
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + GET + FILE_INITIAL)
    public void getVotesByUserNoneAvailable() {
        List<Vote> votes = voteService.getVotesForUser(2);
        assertEquals(0, votes.size());
        verifyNoInteractions(authService);
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
    @ExpectedDatabase(value = FILE_PATH + UPDATE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void updateVote() {
        
        Vote oldVote = voteService.getVoteById(1);
        
        VoteConfig voteConfig = new VoteConfig(
            new VoteOptionConfig(true, false, false, false, false, false),
            DecisionType.DEFAULT
        );
        
        Vote vote = new Vote();
        vote.setId(1);
        vote.setTitle(new EncryptedString("IKS-Thementag", false));
        vote.setDeadline(new Date(DATE_2050_10_10));
        vote.setDescription(new EncryptedString("Fun with code", false));
        vote.setNotificationSettings(new VoteNotificationSettings(
            IKS_MAIL.getBytes(StandardCharsets.UTF_8),
            true,
            false,
            true
        ));
        vote.setVoteConfig(voteConfig);
        
        VoteParticipant voteParticipant = new VoteParticipant();
        voteParticipant.setId(1);
        voteParticipant.setName(new EncryptedString("RubberBandMan", false));
        voteParticipant.setVote(vote);
        
        List<VoteParticipant> voteParticipants = new ArrayList<>();
        voteParticipants.add(voteParticipant);
        
        vote.setParticipants(voteParticipants);
        
        VoteOption voteOption1 = new VoteOption(new EncryptedString("universe", false), vote, false, null);
        voteOption1.setVote(vote);
        
        VoteOption voteOption2 = new VoteOption(new EncryptedString("earth", false), vote, false, null);
        voteOption2.setVote(vote);
        
        VoteOption voteOption3 = new VoteOption(new EncryptedString("spaceship", false), vote, false, null);
        voteOption3.setVote(vote);
        
        vote.setOptions(voteOptionList(voteOption1, voteOption2, voteOption3));
        
        voteService.updateVote(oldVote, vote);
        verifyNoInteractions(authService);
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
    public void updateVoteShouldFailNoOptions() {
        
        Vote oldVote = voteService.getVoteById(1);
        
        Vote vote = createSimpleVote();
        vote.setOptions(new ArrayList<>());
        
        // has no options
        assertThrows(MalformedException.class, () -> voteService.updateVote(oldVote, vote));
        verifyNoInteractions(authService);
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + UPDATE + EXPIRED + FILE_INITIAL)
    @ExpectedDatabase(
        value = FILE_PATH + UPDATE + EXPIRED + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT
    )
    public void updateVoteNewExpiredDate() {
        
        Vote oldVote = voteService.getVoteById(1);
        
        Vote vote = voteService.getVoteById(1);
        
        vote.setDeadline(new Date(DATE_2050_10_10));
        voteService.updateVote(oldVote, vote);
        verifyNoInteractions(authService);
    }
    
    @Test
    public void createVoteShouldFailMisconfiguredParticipantLimitActiveFalseParticipantLimitNotNull() {
        VoteConfig voteConfig = new VoteConfig(
            new VoteOptionConfig(true, false, false, false, false, false),
            DecisionType.DEFAULT
        );
        
        Vote vote = new Vote();
        vote.setId(1);
        vote.setTitle(new EncryptedString("IKS-Thementag", false));
        vote.setDeadline(new Date(DATE_2050_10_10));
        vote.setDescription(new EncryptedString("Fun with code", false));
        vote.setVoteConfig(voteConfig);
        
        VoteOption voteOption = new VoteOption(new EncryptedString("universe", false), vote);
        
        voteOption.setVote(vote);
        
        vote.setOptions(voteOptionList(voteOption));
        
        voteOption.setVote(vote);
        voteOption.setParticipantLimit(10);
        voteOption.setParticipantLimitActive(false);
        assertThrows(MalformedException.class, () -> voteService.createVote(vote));
        verifyNoInteractions(authService);
    }
    
    @Test
    public void createVoteShouldFailParticipantLimitActiveFalseParticipationLimitNull() {
        VoteConfig voteConfig = new VoteConfig(
            new VoteOptionConfig(true, false, false, false, false, false),
            DecisionType.DEFAULT
        );
        
        Vote vote = new Vote();
        vote.setId(1);
        vote.setTitle(new EncryptedString("IKS-Thementag", false));
        vote.setDeadline(new Date(DATE_2050_10_10));
        vote.setDescription(new EncryptedString("Fun with code", false));
        vote.setVoteConfig(voteConfig);
        
        VoteOption voteOption = new VoteOption(new EncryptedString("universe", false), vote);
        
        voteOption.setParticipantLimitActive(true);
        voteOption.setParticipantLimit(null);
        voteOption.setVote(vote);
        
        vote.setOptions(voteOptionList(voteOption));
        
        voteOption.setVote(vote);
        assertThrows(MalformedException.class, () -> voteService.createVote(vote));
        verifyNoInteractions(authService);
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
    public void updateVoteShouldFailMisconfiguredParticipantLimitActiveFalseParticipantLimitNotNull() {
        Vote oldVote = voteService.getVoteById(1);
        
        VoteConfig voteConfig = new VoteConfig(
            new VoteOptionConfig(true, false, false, false, false, false),
            DecisionType.DEFAULT
        );
        
        Vote vote = new Vote();
        vote.setId(1);
        vote.setTitle(new EncryptedString("IKS-Thementag", false));
        vote.setDeadline(new Date(DATE_2050_10_10));
        vote.setDescription(new EncryptedString("Fun with code", false));
        vote.setVoteConfig(voteConfig);
        
        VoteParticipant voteParticipant = new VoteParticipant();
        voteParticipant.setId(1);
        voteParticipant.setName(new EncryptedString("RubberBandMan", false));
        voteParticipant.setVote(vote);
        
        List<VoteParticipant> voteParticipants = new ArrayList<>();
        voteParticipants.add(voteParticipant);
        
        vote.setParticipants(voteParticipants);
        
        VoteOption voteOption1 = new VoteOption(new EncryptedString("universe", false), vote);
        voteOption1.setVote(vote);
        
        VoteOption voteOption2 = new VoteOption(new EncryptedString("earth", false), vote, false, 5);
        voteOption2.setVote(vote);
        
        vote.setOptions(voteOptionList(voteOption1, voteOption2));
        assertThrows(MalformedException.class, () -> voteService.updateVote(oldVote, vote));
        verifyNoInteractions(authService);
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
    public void updateVoteShouldFailParticipantLimitNullParticipationLimitActive() {
        Vote oldVote = voteService.getVoteById(1);
        
        VoteConfig voteConfig = new VoteConfig(
            new VoteOptionConfig(true, false, false, false, false, false),
            DecisionType.DEFAULT
        );
        
        Vote vote = new Vote();
        vote.setId(1);
        vote.setTitle(new EncryptedString("IKS-Thementag", false));
        vote.setDeadline(new Date(DATE_2050_10_10));
        vote.setDescription(new EncryptedString("Fun with code", false));
        vote.setVoteConfig(voteConfig);
        
        VoteParticipant voteParticipant = new VoteParticipant();
        voteParticipant.setId(1);
        voteParticipant.setName(new EncryptedString("RubberBandMan", false));
        voteParticipant.setVote(vote);
        
        List<VoteParticipant> voteParticipants = new ArrayList<>();
        voteParticipants.add(voteParticipant);
        
        vote.setParticipants(voteParticipants);
        
        VoteOption voteOption1 = new VoteOption(new EncryptedString("universe", false), vote, true, null);
        voteOption1.setVote(vote);
        
        VoteOption voteOption2 = new VoteOption(new EncryptedString("earth", false), vote);
        voteOption2.setVote(vote);
        
        vote.setOptions(voteOptionList(voteOption1, voteOption2));
        
        assertThrows(MalformedException.class, () -> voteService.updateVote(oldVote, vote));
        verifyNoInteractions(authService);
    }
    
    @Test
    @DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
    public void yesVotesResetWhenNewVoteLimitLessThanVoteCount() {
        
        Vote oldVote = voteService.getVoteById(1);
        
        VoteConfig voteConfig = new VoteConfig(
            new VoteOptionConfig(true, false, false, false, false, false),
            DecisionType.DEFAULT
        );
        
        Vote vote = new Vote();
        vote.setId(1);
        vote.setTitle(new EncryptedString("IKS-Thementag", false));
        vote.setDeadline(new Date(DATE_2050_10_10));
        vote.setDescription(new EncryptedString("Fun with code", false));
        vote.setVoteConfig(voteConfig);
        
        VoteParticipant voteParticipant = new VoteParticipant();
        voteParticipant.setId(1);
        voteParticipant.setName(new EncryptedString("RubberBandMan", false));
        voteParticipant.setVote(vote);
        
        List<VoteParticipant> voteParticipants = new ArrayList<>();
        voteParticipants.add(voteParticipant);
        
        vote.setParticipants(voteParticipants);
        
        VoteOption voteOption1 = new VoteOption(new EncryptedString("universe", false), vote, true, 1);
        voteOption1.setVote(vote);
        
        vote.setOptions(voteOptionList(voteOption1));
        
        Vote updatedVote = voteService.updateVote(oldVote, vote);
        assertTrue(updatedVote.getParticipants()
            .stream()
            .flatMap(p -> p.getVoteDecisions().stream())
            .filter(d -> d.getDecision().getValue() == 1)
            .noneMatch(d -> d.getVoteOption().getId() == 1));
        verifyNoInteractions(authService);
    }
}