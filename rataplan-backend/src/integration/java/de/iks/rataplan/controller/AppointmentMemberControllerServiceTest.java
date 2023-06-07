package de.iks.rataplan.controller;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.iks.rataplan.config.AppConfig;
import de.iks.rataplan.config.IntegrationConfig;
import de.iks.rataplan.domain.AuthUser;
import de.iks.rataplan.dto.VoteDecisionDTO;
import de.iks.rataplan.dto.VoteParticipantDTO;
import de.iks.rataplan.utils.CookieBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import static de.iks.rataplan.testutils.ITConstants.*;

@ActiveProfiles("integration")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class, IntegrationConfig.class})
@WebAppConfiguration
@Transactional
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class})
public class AppointmentMemberControllerServiceTest {

    private static final String FILE_PATH = PATH + APPOINTMENTMEMBERS;

    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    private MockMvc mockMvc;
    private MockRestServiceServer mockRestServiceServer;

    @Autowired
    private CookieBuilder cookieBuilder;

    @Autowired
    private RestOperations restOperations;

    @Resource
    private WebApplicationContext webApplicationContext;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockRestServiceServer = MockRestServiceServer.createServer((RestTemplate) restOperations);
    }

    @Test
    @DatabaseSetup(FILE_PATH + CREATE + FILE_INITIAL)
    @ExpectedDatabase(value = FILE_PATH + CREATE
            + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void addVoteParticipant() throws Exception {

        VoteParticipantDTO voteParticipantDTO = createSimpleVoteParticipant();

        String json = gson.toJson(voteParticipantDTO);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS);
        requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
        requestBuilder.content(json.getBytes());

        this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DatabaseSetup(FILE_PATH + CREATE + FILE_INITIAL)
    @ExpectedDatabase(value = FILE_PATH + CREATE
            + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void addVoteParticipantWithUserId() throws Exception {

        VoteParticipantDTO voteParticipantDTO = createSimpleVoteParticipant();
        voteParticipantDTO.setUserId(1);

        String json = gson.toJson(voteParticipantDTO);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS);
        requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
        requestBuilder.content(json.getBytes());

        this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DatabaseSetup(FILE_PATH + CREATE + JWTTOKEN + FILE_INITIAL)
    @ExpectedDatabase(value = FILE_PATH + CREATE + JWTTOKEN
            + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void addVoteParticipantWithJwtToken() throws Exception {
		this.setMockRestServiceServer(AUTHUSER_1);

		VoteParticipantDTO voteParticipantDTO = createSimpleVoteParticipant();
		voteParticipantDTO.setName(AUTHUSER_1.getUsername());

		String json = gson.toJson(voteParticipantDTO);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.post(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());
		requestBuilder.cookie(cookieBuilder.generateCookie(JWTTOKEN_VALUE, false));

		this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DatabaseSetup(FILE_PATH + CREATE + JWTTOKEN + PARTICIPATE_TWICE + FILE_INITIAL)
    public void addVoteParticipantWithJwtTokenTwice() throws Exception {
        this.setMockRestServiceServer(AUTHUSER_1);

        VoteParticipantDTO voteParticipantDTO = createSimpleVoteParticipant();
        voteParticipantDTO.setName(AUTHUSER_1.getUsername());

        String json = gson.toJson(voteParticipantDTO);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS);
        requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
        requestBuilder.content(json.getBytes());
        requestBuilder.cookie(cookieBuilder.generateCookie(JWTTOKEN_VALUE, false));

        this.mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    private VoteParticipantDTO createSimpleVoteParticipant() {
		VoteParticipantDTO voteParticipantDTO = new VoteParticipantDTO();
		voteParticipantDTO.setAppointmentRequestId(1);
		voteParticipantDTO.setName("IKS");

		List<VoteDecisionDTO> decisions = new ArrayList<>();

		decisions.add(new VoteDecisionDTO(1, 1, 0, null));
		decisions.add(new VoteDecisionDTO(2, 1, 1, null));

		voteParticipantDTO.setDecisions(decisions);
		return voteParticipantDTO;
	}

    private void setMockRestServiceServer(AuthUser authUser) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("jwttoken", "value of returned token");

        mockRestServiceServer.expect(MockRestRequestMatchers.requestTo(AUTH_SERVICE_URL + "/users/profile"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                        .body(gson.toJson(authUser)).headers(responseHeaders));
    }
}
