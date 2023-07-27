package de.iks.rataplan.repository;

import static de.iks.rataplan.testutils.TestConstants.*;
import static de.iks.rataplan.utils.VoteBuilder.voteOptionList;
import static org.junit.Assert.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import de.iks.rataplan.domain.*;
import de.iks.rataplan.service.CryptoServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class, TestConfig.class })
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class VoteRepositoryTest {

	private static final String FILE_PATH = PATH + REPOSITORY + VOTES;

	@Autowired
	private VoteRepository voteRepository;
	@Autowired
	private CryptoServiceImpl cryptoService;

	@Test
	@DatabaseSetup(FILE_EMPTY_DB)
	@ExpectedDatabase(value = FILE_PATH + CREATE + "/simple"
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void createVoteWithDefaultConfigAndTwoOptions() throws Exception {
		System.out.print(cryptoService.decryptDB(voteRepository.getOne(14).getParticipationToken()));
		Vote vote = createSimpleVote();

		voteRepository.saveAndFlush(vote);
	}

	@Test
	@DatabaseSetup(FILE_EMPTY_DB)
	@ExpectedDatabase(value = FILE_PATH + CREATE + "/extended"
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void createVoteWithExtendedConfigAndOneOption() throws Exception {
		Vote vote = new Vote(new EncryptedString("Coding Dojo", false),
				new EncryptedString("Fun with code", false), new Date(DATE_2050_10_10),
				new EncryptedString(IKS_NAME, false), new EncryptedString(IKS_MAIL, false), new VoteConfig(
						new VoteOptionConfig(true, true, true, true, true, true), DecisionType.EXTENDED));

		VoteOption voteOption = new VoteOption(new EncryptedString("Let's Do Something", false), vote);
		voteOption.setDescription(new EncryptedString("Let's Do Something", false));
		voteOption.setUrl(new EncryptedString("www.maybe.here", false));
		voteOption.setStartDate(new Timestamp(DATE_2050_11_11__11_11_00));
		voteOption.setEndDate(new Timestamp(DATE_2050_12_12__12_12_00));

		vote.setOptions(voteOptionList(voteOption));

		voteRepository.saveAndFlush(vote);
	}

	@Test
	@DatabaseSetup(FILE_PATH + CREATE + "/simpleWithUser" + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + CREATE + "/simpleWithUser"
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void createVoteWithUserAndDefaultConfigAndTwoOptions() throws Exception {

		Vote vote = createSimpleVote();

		vote.setUserId(1);

		voteRepository.saveAndFlush(vote);
	}

	@Test
	@DatabaseSetup(FILE_PATH + GET + "/simple" + FILE_INITIAL)
	public void getVoteById() throws Exception {

		Vote vote = voteRepository.findOne(1);
		assertEquals(DecisionType.DEFAULT, vote.getVoteConfig().getDecisionType());
		assertTrue(vote.getVoteConfig().getVoteOptionConfig().isDescription());
		assertFalse(vote.getVoteConfig().getVoteOptionConfig().isStartDate());
		assertFalse(vote.getVoteConfig().getVoteOptionConfig().isStartTime());
		assertFalse(vote.getVoteConfig().getVoteOptionConfig().isEndDate());
		assertFalse(vote.getVoteConfig().getVoteOptionConfig().isEndTime());
		assertFalse(vote.getVoteConfig().getVoteOptionConfig().isUrl());

		assertEquals(1, vote.getId().intValue());
		assertEquals("Coding Dojo", vote.getTitle().getString());
		assertEquals("Fun with code", vote.getDescription().getString());
		assertEquals(IKS_MAIL, vote.getOrganizerMail().getString());
		assertFalse(vote.isNotified());

		assertEquals(2, vote.getOptions().size());
	}

	@Test
	@DatabaseSetup(FILE_PATH + GET + "/simpleThreeRequests" + FILE_INITIAL)
	public void getAllVotes() throws Exception {

		List<Vote> votes = voteRepository.findAll();

		assertEquals(3, votes.size());
		assertEquals("Coding Dojo 1", votes.get(0).getTitle().getString());
		assertEquals("Coding Dojo 2", votes.get(1).getTitle().getString());
		assertEquals("Coding Dojo 3", votes.get(2).getTitle().getString());
	}

	@Test
	@DatabaseSetup(FILE_PATH + GET + "/simpleThreeRequests" + FILE_INITIAL)
	public void getAllVotesByUserId() throws Exception {

		List<Vote> votes = voteRepository.findAllByUserId(1);

		assertEquals(2, votes.size());
		assertEquals("Coding Dojo 1", votes.get(0).getTitle().getString());
		assertEquals("Coding Dojo 3", votes.get(1).getTitle().getString());
	}

	@Test
	@DatabaseSetup(FILE_PATH + GET + "/backendUserWithoutRequests" + FILE_INITIAL)
	public void getAllVotesByUserIdNoVotes() throws Exception {

		List<Vote> votes = voteRepository.findAllByUserId(1);

		assertEquals(0, votes.size());
	}

	@Test
	@DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateVote() throws Exception {
		Vote vote = voteRepository.findOne(1);

		vote.setTitle(new EncryptedString("IKS-Thementag", false));

		voteRepository.saveAndFlush(vote);
	}

	@Test(expected = DataIntegrityViolationException.class)
	@DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateVoteShouldFailNoDeadline() throws Exception {
		Vote vote = voteRepository.findOne(1);

		vote.setTitle(new EncryptedString("IKS-Thementag", false));
		vote.setDeadline(null);

		voteRepository.saveAndFlush(vote);
	}

}
