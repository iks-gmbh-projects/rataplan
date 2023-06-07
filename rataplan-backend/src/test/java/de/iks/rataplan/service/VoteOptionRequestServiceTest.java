package de.iks.rataplan.service;

import static de.iks.rataplan.testutils.TestConstants.VOTES;
import static de.iks.rataplan.testutils.TestConstants.CREATE;
import static de.iks.rataplan.testutils.TestConstants.DATE_2050_10_10;
import static de.iks.rataplan.testutils.TestConstants.EXPIRED;
import static de.iks.rataplan.testutils.TestConstants.FILE_EMPTY_DB;
import static de.iks.rataplan.testutils.TestConstants.FILE_EXPECTED;
import static de.iks.rataplan.testutils.TestConstants.FILE_INITIAL;
import static de.iks.rataplan.testutils.TestConstants.GET;
import static de.iks.rataplan.testutils.TestConstants.IKS_MAIL;
import static de.iks.rataplan.testutils.TestConstants.PATH;
import static de.iks.rataplan.testutils.TestConstants.SERVICE;
import static de.iks.rataplan.testutils.TestConstants.UPDATE;
import static de.iks.rataplan.testutils.TestConstants.createSimpleVote;
import static de.iks.rataplan.utils.VoteBuilder.voteOptionList;
import static org.junit.Assert.assertEquals;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import de.iks.rataplan.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import de.iks.rataplan.config.AppConfig;
import de.iks.rataplan.config.TestConfig;
import de.iks.rataplan.exceptions.MalformedException;
import de.iks.rataplan.exceptions.ResourceNotFoundException;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class, TestConfig.class })
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class VoteOptionRequestServiceTest {

	private static final String FILE_PATH = PATH + SERVICE + VOTES;

	@Autowired
	private VoteService voteService;

	@Test
	@DatabaseSetup(FILE_EMPTY_DB)
	@ExpectedDatabase(value = FILE_PATH + CREATE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void createAppointmentRequest() throws Exception {
		Vote vote = createSimpleVote();

		voteService.createVote(vote);
	}

	@Test(expected = MalformedException.class)
	@DatabaseSetup(FILE_EMPTY_DB)
	public void createAppointmentRequestShouldFailHasNoAppointments() throws Exception {
		Vote vote = createSimpleVote();
		vote.setOptions(new ArrayList<VoteOption>());

		voteService.createVote(vote);
	}

	@Test(expected = MalformedException.class)
	@DatabaseSetup(FILE_EMPTY_DB)
	public void createAppointmentRequestShouldFailHasMember() throws Exception {
		Vote vote = createSimpleVote();

		List<VoteParticipant> voteParticipants = vote.getParticipants();

		VoteParticipant voteParticipant = new VoteParticipant();
		voteParticipant.setName(new EncryptedString("Fritz macht den Fehler", false));
		voteParticipants.add(voteParticipant);

		vote.setParticipants(voteParticipants);

		voteService.createVote(vote);
	}

	@Test(expected = MalformedException.class)
	@DatabaseSetup(FILE_EMPTY_DB)
	public void createAppointmentRequestShouldFailWrongAppointmentConfig() throws Exception {
		Vote vote = createSimpleVote();

		VoteOption voteOption = new VoteOption(new EncryptedString("iks Hilden", false), vote);
		voteOption.setUrl(new EncryptedString("thiswontwork.com", false));

		vote
				.setOptions(voteOptionList(voteOption, new VoteOption(new EncryptedString("homeoffice", false),
					vote
				)));

		voteService.createVote(vote);
	}

	@Test
	@DatabaseSetup(FILE_PATH + GET + FILE_INITIAL)
	public void getAllAppointmentRequests() throws Exception {
		List<Vote> votes = voteService.getVotes();
		assertEquals(4, votes.size());
	}

	@Test
	@DatabaseSetup(FILE_EMPTY_DB)
	public void getAppointmentRequestsNoneAvailable() throws Exception {
		List<Vote> votes = voteService.getVotes();
		assertEquals(0, votes.size());
	}

	@Test
	@DatabaseSetup(FILE_PATH + GET + FILE_INITIAL)
	public void getAppointmentRequestById() throws Exception {
		Vote vote = voteService.getVoteById(1);
		assertEquals(vote.getTitle().getString(), "Coding Dojo");
		assertEquals(vote.getOptions().size(), 2);
		assertEquals(vote.getParticipants().size(), 0);
	}

	@Test(expected = ResourceNotFoundException.class)
	@DatabaseSetup(FILE_EMPTY_DB)
	public void getAppointmentRequestByIdShouldFailDoesNotExist() throws Exception {
		voteService.getVoteById(1);

	}

	@Test
	@DatabaseSetup(FILE_PATH + GET + FILE_INITIAL)
	public void getAppointmentRequestsByUser() throws Exception {
		List<Vote> votes = voteService.getVotesForUser(1);
		assertEquals(2, votes.size());
	}

	@Test
	@DatabaseSetup(FILE_PATH + GET + FILE_INITIAL)
	public void getAppointmentRequestsByUserNoneAvailable() throws Exception {
		List<Vote> votes = voteService.getVotesForUser(2);
		assertEquals(0, votes.size());
	}

	@Test
	@DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateAppointmentRequest() throws Exception {
		
		Vote oldVote = voteService.getVoteById(1);
		
		VoteConfig voteConfig = new VoteConfig(
				new VoteOptionConfig(true, false, false, false, false, false), DecisionType.DEFAULT);
		voteConfig.setId(1);

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

		VoteOption voteOption2 = new VoteOption(new EncryptedString("earth", false), vote);
		voteOption2.setVote(vote);

		VoteOption voteOption3 = new VoteOption(new EncryptedString("spaceship", false), vote);
		voteOption3.setVote(vote);

		vote.setOptions(voteOptionList(voteOption1, voteOption2, voteOption3));

		voteService.updateVote(oldVote, vote);
	}

	@Test(expected = MalformedException.class)
	@DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
	public void updateAppointmentRequestShouldFailNoAppointment() throws Exception {
		
		Vote oldVote = voteService.getVoteById(1);
		
		Vote vote = createSimpleVote();
		vote.setOptions(new ArrayList<VoteOption>());

		// has no appointments
		voteService.updateVote(oldVote, vote);
	}

	@Test
	@DatabaseSetup(FILE_PATH + UPDATE + EXPIRED + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE + EXPIRED
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateAppointmentRequestNewExpiredDate() throws Exception {
		
		Vote oldVote = voteService.getVoteById(1);
		
		Vote vote = voteService.getVoteById(1);

		vote.setDeadline(new Date(DATE_2050_10_10));
		voteService.updateVote(oldVote, vote);
	}

}
