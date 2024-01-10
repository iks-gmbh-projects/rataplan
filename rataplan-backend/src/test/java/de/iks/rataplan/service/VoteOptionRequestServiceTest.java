package de.iks.rataplan.service;

import de.iks.rataplan.domain.*;
import de.iks.rataplan.exceptions.MalformedException;
import de.iks.rataplan.exceptions.ResourceNotFoundException;
import de.iks.rataplan.repository.VoteRepository;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static de.iks.rataplan.testutils.TestConstants.*;
import static de.iks.rataplan.utils.VoteBuilder.voteOptionList;
import static org.junit.Assert.*;

@SpringBootTest
@TestExecutionListeners(
    value = {DbUnitTestExecutionListener.class, TransactionalTestExecutionListener.class},
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@Transactional
public class VoteOptionRequestServiceTest {

	private static final String FILE_PATH = PATH + SERVICE + VOTES;

	@Autowired
	private VoteService voteService;

    @Autowired
    VoteRepository voteRepository;

	@Test
	@DatabaseSetup(FILE_EMPTY_DB)
	@ExpectedDatabase(value = FILE_PATH + CREATE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void createVote() throws Exception {
		Vote vote = createSimpleVote();

		voteService.createVote(vote);
	}

	@Test
	@DatabaseSetup(FILE_EMPTY_DB)
	public void createVoteShouldFailHasNoOptions() throws Exception {
		Vote vote = createSimpleVote();
		vote.setOptions(new ArrayList<>());
        Assertions.assertThrows(MalformedException.class, () -> voteService.createVote(vote));
	}

	@Test
	@DatabaseSetup(FILE_EMPTY_DB)
	public void createVoteShouldFailHasMember() throws Exception {
		Vote vote = createSimpleVote();

		List<VoteParticipant> voteParticipants = vote.getParticipants();

		VoteParticipant voteParticipant = new VoteParticipant();
		voteParticipant.setName(new EncryptedString("Fritz macht den Fehler", false));
		voteParticipants.add(voteParticipant);

		vote.setParticipants(voteParticipants);

		Assertions.assertThrows(MalformedException.class, () -> voteService.createVote(vote));
	}

	@Test
	@DatabaseSetup(FILE_EMPTY_DB)
	public void createVoteShouldFailWrongVoteOptionConfig() throws Exception {
		Vote vote = createSimpleVote();

		VoteOption voteOption = new VoteOption(new EncryptedString("iks Hilden", false), vote);
		voteOption.setUrl(new EncryptedString("thiswontwork.com", false));

		vote
				.setOptions(voteOptionList(voteOption, new VoteOption(new EncryptedString("homeoffice", false),
					vote
				)));

		Assertions.assertThrows(MalformedException.class, () -> voteService.createVote(vote));
	}

	@Test
	@DatabaseSetup(FILE_PATH + GET + FILE_INITIAL)
	public void getAllVotes() throws Exception {
		List<Vote> votes = voteService.getVotes();
		assertEquals(4, votes.size());
	}

	@Test
	@DatabaseSetup(FILE_EMPTY_DB)
	public void getVotesNoneAvailable() throws Exception {
		List<Vote> votes = voteService.getVotes();
		assertEquals(0, votes.size());
	}

	@Test
	@DatabaseSetup(FILE_PATH + GET + FILE_INITIAL)
	public void getVoteById() throws Exception {
		Vote vote = voteService.getVoteById(1);
		assertEquals(vote.getTitle().getString(), "Coding Dojo");
		assertEquals(vote.getOptions().size(), 2);
		assertEquals(vote.getParticipants().size(), 0);
	}

	@Test
	@DatabaseSetup(FILE_EMPTY_DB)
	public void getVoteByIdShouldFailDoesNotExist() throws Exception {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> voteService.getVoteById(1));
	}

	@Test
	@DatabaseSetup(FILE_PATH + GET + FILE_INITIAL)
	public void getVotesByUser() throws Exception {
		List<Vote> votes = voteService.getVotesForUser(1);
		assertEquals(2, votes.size());
	}

	@Test
	@DatabaseSetup(FILE_PATH + GET + FILE_INITIAL)
	public void getVotesByUserNoneAvailable() throws Exception {
		List<Vote> votes = voteService.getVotesForUser(2);
		assertEquals(0, votes.size());
	}

	@Test
	@DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateVote() throws Exception {
		
		Vote oldVote = voteService.getVoteById(1);
		
		VoteConfig voteConfig = new VoteConfig(
				new VoteOptionConfig(true, false, false, false, false, false), DecisionType.DEFAULT);

		Vote vote = new Vote();
		vote.setId(1);
		vote.setTitle(new EncryptedString("IKS-Thementag", false));
		vote.setDeadline(new Date(DATE_2050_10_10));
		vote.setDescription(new EncryptedString("Fun with code", false));
		vote.setOrganizerMail(new EncryptedString(IKS_MAIL, false));
		vote.setVoteConfig(voteConfig);

		VoteParticipant voteParticipant = new VoteParticipant();
		voteParticipant.setId(1);
		voteParticipant.setName(new EncryptedString("RubberBandMan", false));
		voteParticipant.setVote(vote);

		List<VoteParticipant> voteParticipants = new ArrayList<VoteParticipant>();
		voteParticipants.add(voteParticipant);

		vote.setParticipants(voteParticipants);

        VoteOption voteOption1 = new VoteOption(new EncryptedString("universe", false), vote,false,null);
        voteOption1.setVote(vote);

        VoteOption voteOption2 = new VoteOption(new EncryptedString("earth", false), vote,false,null);
        voteOption2.setVote(vote);

        VoteOption voteOption3 = new VoteOption(new EncryptedString("spaceship", false), vote, false,null);
        voteOption3.setVote(vote);

		vote.setOptions(voteOptionList(voteOption1, voteOption2, voteOption3));

		voteService.updateVote(oldVote, vote);
	}

	@Test
	@DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
	public void updateVoteShouldFailNoOptions() throws Exception {
		
		Vote oldVote = voteService.getVoteById(1);
		
		Vote vote = createSimpleVote();
		vote.setOptions(new ArrayList<>());

		// has no options
		Assertions.assertThrows(MalformedException.class, () -> voteService.updateVote(oldVote, vote));
	}

	@Test
	@DatabaseSetup(FILE_PATH + UPDATE + EXPIRED + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE + EXPIRED
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateVoteNewExpiredDate() throws Exception {
		
		Vote oldVote = voteService.getVoteById(1);
		
		Vote vote = voteService.getVoteById(1);

		vote.setDeadline(new Date(DATE_2050_10_10));
		voteService.updateVote(oldVote, vote);
	}

    @Test
    public void createVoteShouldFailMisconfiguredParticipantLimitActiveFalseParticipantLimitNotNull() {
        VoteConfig voteConfig = new VoteConfig(
                new VoteOptionConfig(true, false, false, false, false, false), DecisionType.DEFAULT);

        Vote vote = new Vote();
        vote.setId(1);
        vote.setTitle(new EncryptedString("IKS-Thementag", false));
        vote.setDeadline(new Date(DATE_2050_10_10));
        vote.setDescription(new EncryptedString("Fun with code", false));
        vote.setOrganizerMail(new EncryptedString(IKS_MAIL, false));
        vote.setVoteConfig(voteConfig);

        VoteOption voteOption = new VoteOption(new EncryptedString("universe", false), vote);

        voteOption.setVote(vote);

        vote.setOptions(voteOptionList(voteOption));

        voteOption.setVote(vote);
        voteOption.setParticipantLimit(10);
        voteOption.setParticipantLimitActive(false);
        Assertions.assertThrows(MalformedException.class,() -> voteService.createVote(vote));
    }

    @Test
    public void createVoteShouldFailParticipantLimitActiveFalseParticipationLimitNull() {
        VoteConfig voteConfig = new VoteConfig(
                new VoteOptionConfig(true, false, false, false, false, false), DecisionType.DEFAULT);

        Vote vote = new Vote();
        vote.setId(1);
        vote.setTitle(new EncryptedString("IKS-Thementag", false));
        vote.setDeadline(new Date(DATE_2050_10_10));
        vote.setDescription(new EncryptedString("Fun with code", false));
        vote.setOrganizerMail(new EncryptedString(IKS_MAIL, false));
        vote.setVoteConfig(voteConfig);

        VoteOption voteOption = new VoteOption(new EncryptedString("universe", false), vote);

        voteOption.setParticipantLimitActive(true);
        voteOption.setParticipantLimit(null);
        voteOption.setVote(vote);

        vote.setOptions(voteOptionList(voteOption));

        voteOption.setVote(vote);
        Assertions.assertThrows(MalformedException.class,() -> voteService.createVote(vote));
    }


    @Test
    @DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
    public void updateVoteShouldFailMisconfiguredParticipantLimitActiveFalseParticipantLimitNotNull() {
        Vote oldVote = voteService.getVoteById(1);

        VoteConfig voteConfig = new VoteConfig(
                new VoteOptionConfig(true, false, false, false, false, false), DecisionType.DEFAULT);

        Vote vote = new Vote();
        vote.setId(1);
        vote.setTitle(new EncryptedString("IKS-Thementag", false));
        vote.setDeadline(new Date(DATE_2050_10_10));
        vote.setDescription(new EncryptedString("Fun with code", false));
        vote.setOrganizerMail(new EncryptedString(IKS_MAIL, false));
        vote.setVoteConfig(voteConfig);

        VoteParticipant voteParticipant = new VoteParticipant();
        voteParticipant.setId(1);
        voteParticipant.setName(new EncryptedString("RubberBandMan", false));
        voteParticipant.setVote(vote);

        List<VoteParticipant> voteParticipants = new ArrayList<VoteParticipant>();
        voteParticipants.add(voteParticipant);

        vote.setParticipants(voteParticipants);

        VoteOption voteOption1 = new VoteOption(new EncryptedString("universe", false), vote);
        voteOption1.setVote(vote);

        VoteOption voteOption2 = new VoteOption(new EncryptedString("earth", false), vote,false,5);
        voteOption2.setVote(vote);

        vote.setOptions(voteOptionList(voteOption1, voteOption2));
        Assertions.assertThrows(MalformedException.class,() -> voteService.updateVote(oldVote, vote));
    }

    @Test
    @DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
    public void updateVoteShouldFailParticipantLimitNullParticipationLimitActive() {
        Vote oldVote = voteService.getVoteById(1);

        VoteConfig voteConfig = new VoteConfig(
                new VoteOptionConfig(true, false, false, false, false, false), DecisionType.DEFAULT);

        Vote vote = new Vote();
        vote.setId(1);
        vote.setTitle(new EncryptedString("IKS-Thementag", false));
        vote.setDeadline(new Date(DATE_2050_10_10));
        vote.setDescription(new EncryptedString("Fun with code", false));
        vote.setOrganizerMail(new EncryptedString(IKS_MAIL, false));
        vote.setVoteConfig(voteConfig);

        VoteParticipant voteParticipant = new VoteParticipant();
        voteParticipant.setId(1);
        voteParticipant.setName(new EncryptedString("RubberBandMan", false));
        voteParticipant.setVote(vote);

        List<VoteParticipant> voteParticipants = new ArrayList<VoteParticipant>();
        voteParticipants.add(voteParticipant);

        vote.setParticipants(voteParticipants);

        VoteOption voteOption1 = new VoteOption(new EncryptedString("universe", false), vote, true, null);
        voteOption1.setVote(vote);

        VoteOption voteOption2 = new VoteOption(new EncryptedString("earth", false), vote);
        voteOption2.setVote(vote);

        vote.setOptions(voteOptionList(voteOption1, voteOption2));
        
        Assertions.assertThrows(MalformedException.class,() -> voteService.updateVote(oldVote, vote));
    }

    @Test
    @DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
    public void yesVotesResetWhenNewVoteLimitLessThanVoteCount() {

        Vote oldVote = voteService.getVoteById(1);

        VoteConfig voteConfig = new VoteConfig(
                new VoteOptionConfig(true, false, false, false, false, false), DecisionType.DEFAULT);

        Vote vote = new Vote();
        vote.setId(1);
        vote.setTitle(new EncryptedString("IKS-Thementag", false));
        vote.setDeadline(new Date(DATE_2050_10_10));
        vote.setDescription(new EncryptedString("Fun with code", false));
        vote.setOrganizerMail(new EncryptedString(IKS_MAIL, false));
        vote.setVoteConfig(voteConfig);

        VoteParticipant voteParticipant = new VoteParticipant();
        voteParticipant.setId(1);
        voteParticipant.setName(new EncryptedString("RubberBandMan", false));
        voteParticipant.setVote(vote);

        List<VoteParticipant> voteParticipants = new ArrayList<VoteParticipant>();
        voteParticipants.add(voteParticipant);

        vote.setParticipants(voteParticipants);

        VoteOption voteOption1 = new VoteOption(new EncryptedString("universe", false), vote, true,1);
        voteOption1.setVote(vote);

        vote.setOptions(voteOptionList(voteOption1));

        Vote updatedVote = voteService.updateVote(oldVote, vote);
        assertTrue(
                updatedVote.getParticipants()
                        .stream()
                        .flatMap(p -> p.getVoteDecisions().stream())
                        .filter(d -> d.getDecision().getValue() == 1)
                        .noneMatch(d -> d.getVoteOption().getId() == 1)
        );
    }


}
