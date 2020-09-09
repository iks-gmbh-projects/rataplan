package de.iks.rataplan.controller;

import static de.iks.rataplan.testutils.ITConstants.ACCESS_TOKEN_ADMIN_PASSWORD;
import static de.iks.rataplan.testutils.ITConstants.ACCESS_TOKEN_PASSWORD;
import static de.iks.rataplan.testutils.ITConstants.ACCESS_TOKEN_WRONG_PASSWORD;
import static de.iks.rataplan.testutils.ITConstants.APPOINTMENTMEMBERS;
import static de.iks.rataplan.testutils.ITConstants.APPOINTMENTREQUESTS;
import static de.iks.rataplan.testutils.ITConstants.AUTHUSER_1;
import static de.iks.rataplan.testutils.ITConstants.AUTHUSER_2;
import static de.iks.rataplan.testutils.ITConstants.AUTH_SERVICE_URL;
import static de.iks.rataplan.testutils.ITConstants.COOKIE_JWTTOKEN;
import static de.iks.rataplan.testutils.ITConstants.CREATE;
import static de.iks.rataplan.testutils.ITConstants.DELETE;
import static de.iks.rataplan.testutils.ITConstants.FILE_EXPECTED;
import static de.iks.rataplan.testutils.ITConstants.FILE_INITIAL;
import static de.iks.rataplan.testutils.ITConstants.HEADER_ACCESS_TOKEN;
import static de.iks.rataplan.testutils.ITConstants.JWTTOKEN;
import static de.iks.rataplan.testutils.ITConstants.JWTTOKEN_VALUE;
import static de.iks.rataplan.testutils.ITConstants.PASSWORD;
import static de.iks.rataplan.testutils.ITConstants.PATH;
import static de.iks.rataplan.testutils.ITConstants.UPDATE;
import static de.iks.rataplan.testutils.ITConstants.VERSION;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

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

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.iks.rataplan.config.AppConfig;
import de.iks.rataplan.config.IntegrationConfig;
import de.iks.rataplan.domain.AuthUser;
import de.iks.rataplan.dto.AppointmentDecisionDTO;
import de.iks.rataplan.dto.AppointmentMemberDTO;
import de.iks.rataplan.utils.CookieBuilder;

@ActiveProfiles("integration")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class, IntegrationConfig.class })
@WebAppConfiguration
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class AppointmentMemberIT {

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
	public void addAppointmentMember() throws Exception {

		AppointmentMemberDTO appointmentMemberDTO = createSimpleAppointmentMember();

		String json = gson.toJson(appointmentMemberDTO);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.post(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isCreated());
	}

	@Test
	@DatabaseSetup(FILE_PATH + CREATE + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + CREATE
			+ FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void addAppointmentMemberShouldFailTooManyDecisions() throws Exception {

		AppointmentMemberDTO appointmentMemberDTO = this.createSimpleAppointmentMember();

		List<AppointmentDecisionDTO> appointmentDecisions = appointmentMemberDTO.getAppointmentDecisions();

		AppointmentDecisionDTO appointmentDecision = new AppointmentDecisionDTO();
		appointmentDecision.setDecision(1);
		appointmentDecision.setAppointmentId(3);
		appointmentDecisions.add(appointmentDecision);

		appointmentMemberDTO.setAppointmentDecisions(appointmentDecisions);

		String json = gson.toJson(appointmentMemberDTO);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.post(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	@DatabaseSetup(FILE_PATH + CREATE + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + CREATE
			+ FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void addAppointmentMemberShouldFailWrongDecisionType() throws Exception {

		AppointmentMemberDTO appointmentMemberDTO = this.createSimpleAppointmentMember();

		List<AppointmentDecisionDTO> appointmentDecisions = appointmentMemberDTO.getAppointmentDecisions();

		appointmentDecisions.set(0, new AppointmentDecisionDTO(1, null, null, 5));
		appointmentDecisions.set(1, new AppointmentDecisionDTO(2, null, null, 19));

		appointmentMemberDTO.setAppointmentDecisions(appointmentDecisions);

		String json = gson.toJson(appointmentMemberDTO);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.post(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	@DatabaseSetup(FILE_PATH + CREATE + PASSWORD + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + CREATE + PASSWORD
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void addAppointmentMemberWithPassword() throws Exception {

		AppointmentMemberDTO appointmentMemberDTO = createSimpleAppointmentMember();

		String json = gson.toJson(appointmentMemberDTO);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.post(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());
		requestBuilder.header(HEADER_ACCESS_TOKEN, ACCESS_TOKEN_PASSWORD);

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isCreated());
	}

	@Test
	@DatabaseSetup(FILE_PATH + CREATE + PASSWORD + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + CREATE + PASSWORD
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void addAppointmentMemberWithAdminPassword() throws Exception {

		AppointmentMemberDTO appointmentMemberDTO = createSimpleAppointmentMember();

		String json = gson.toJson(appointmentMemberDTO);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.post(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());
		requestBuilder.header(HEADER_ACCESS_TOKEN, ACCESS_TOKEN_ADMIN_PASSWORD);

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isCreated());
	}

	@Test
	@DatabaseSetup(FILE_PATH + CREATE + PASSWORD + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + CREATE + PASSWORD
			+ FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void addAppointmentMemberWithWrongPasswordShouldFail() throws Exception {

		AppointmentMemberDTO appointmentMemberDTO = createSimpleAppointmentMember();

		String json = gson.toJson(appointmentMemberDTO);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.post(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());
		requestBuilder.header(HEADER_ACCESS_TOKEN, ACCESS_TOKEN_WRONG_PASSWORD);

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@DatabaseSetup(FILE_PATH + CREATE + JWTTOKEN + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + CREATE + JWTTOKEN
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void addAppointmentMemberWithJWTToken() throws Exception {

		this.setMockRestServiceServer(AUTHUSER_1);

		AppointmentMemberDTO appointmentMemberDTO = createSimpleAppointmentMember();
		appointmentMemberDTO.setName(AUTHUSER_1.getUsername());

		String json = gson.toJson(appointmentMemberDTO);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.post(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());
		requestBuilder.cookie(cookieBuilder.createJWTCookie(JWTTOKEN_VALUE, false));

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.cookie().maxAge(COOKIE_JWTTOKEN, 60000));
	}

	@Test
	@DatabaseSetup(FILE_PATH + CREATE + JWTTOKEN + "/samename" + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + CREATE + JWTTOKEN + "/samename"
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void addAppointmentMemberWithJWTTokenAndSameName() throws Exception {

		this.setMockRestServiceServer(AUTHUSER_1);

		AppointmentMemberDTO appointmentMemberDTO = createSimpleAppointmentMember();
		appointmentMemberDTO.setName(AUTHUSER_1.getUsername());

		String json = gson.toJson(appointmentMemberDTO);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.post(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());
		requestBuilder.cookie(cookieBuilder.createJWTCookie(JWTTOKEN_VALUE, false));

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.cookie().maxAge(COOKIE_JWTTOKEN, 60000));
	}

	@Test
	@DatabaseSetup(FILE_PATH + CREATE + JWTTOKEN + PASSWORD + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + CREATE + JWTTOKEN + PASSWORD
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void addAppointmentMemberWithJWTTokenAndWithPassword() throws Exception {

		this.setMockRestServiceServer(AUTHUSER_1);

		AppointmentMemberDTO appointmentMemberDTO = createSimpleAppointmentMember();
		appointmentMemberDTO.setName(AUTHUSER_1.getUsername());

		String json = gson.toJson(appointmentMemberDTO);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.post(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());
		requestBuilder.header(HEADER_ACCESS_TOKEN, ACCESS_TOKEN_PASSWORD);
		requestBuilder.cookie(cookieBuilder.createJWTCookie(JWTTOKEN_VALUE, false));

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.cookie().maxAge(COOKIE_JWTTOKEN, 60000));
	}

	@Test
	@DatabaseSetup(FILE_PATH + CREATE + JWTTOKEN + PASSWORD + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + CREATE + JWTTOKEN + PASSWORD
			+ FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void addAppointmentMemberWithJWTTokenAndWithWrongPasswordShouldFail() throws Exception {

		this.setMockRestServiceServer(AUTHUSER_1);

		AppointmentMemberDTO appointmentMemberDTO = createSimpleAppointmentMember();
		appointmentMemberDTO.setName(AUTHUSER_1.getUsername());

		String json = gson.toJson(appointmentMemberDTO);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.post(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());
		requestBuilder.header(HEADER_ACCESS_TOKEN, ACCESS_TOKEN_PASSWORD + "WRONG");
		requestBuilder.cookie(cookieBuilder.createJWTCookie(JWTTOKEN_VALUE, false));

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.cookie().maxAge(COOKIE_JWTTOKEN, 60000));
	}

	@Test
	@DatabaseSetup(FILE_PATH + CREATE + JWTTOKEN + PASSWORD + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + CREATE + JWTTOKEN + PASSWORD
			+ "/differentname.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void addAppointmentMemberWithJWTTokenAndPasswordAndDifferentName() throws Exception {

		this.setMockRestServiceServer(AUTHUSER_1);

		AppointmentMemberDTO appointmentMemberDTO = createSimpleAppointmentMember();
		appointmentMemberDTO.setName("different name");

		String json = gson.toJson(appointmentMemberDTO);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.post(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());
		requestBuilder.header(HEADER_ACCESS_TOKEN, ACCESS_TOKEN_PASSWORD);
		requestBuilder.cookie(cookieBuilder.createJWTCookie(JWTTOKEN_VALUE, false));

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.cookie().maxAge(COOKIE_JWTTOKEN, 60000));
	}

	@Test
	@DatabaseSetup(FILE_PATH + DELETE + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + DELETE
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void deleteAppointmentMember() throws Exception {
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.delete(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS + "/1");
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isNoContent());
	}

	@Test
	@DatabaseSetup(FILE_PATH + DELETE + PASSWORD + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + DELETE + PASSWORD
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void deleteAppointmentMemberWithPassword() throws Exception {
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.delete(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS + "/1");
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.header(HEADER_ACCESS_TOKEN, ACCESS_TOKEN_PASSWORD);

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isNoContent());
	}

	@Test
	@DatabaseSetup(FILE_PATH + DELETE + PASSWORD + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + DELETE + PASSWORD
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void deleteAppointmentMemberWithAdminPassword() throws Exception {
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.delete(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS + "/1");
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.header(HEADER_ACCESS_TOKEN, ACCESS_TOKEN_ADMIN_PASSWORD);

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isNoContent());
	}

	@Test
	@DatabaseSetup(FILE_PATH + DELETE + PASSWORD + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + DELETE + PASSWORD
			+ FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void deleteAppointmentMemberWithWrongPasswordShouldFail() throws Exception {
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.delete(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS + "/1");
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.header(HEADER_ACCESS_TOKEN, ACCESS_TOKEN_WRONG_PASSWORD);

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@DatabaseSetup(FILE_PATH + DELETE + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + DELETE
			+ FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void deleteAppointmentMemberShouldFailDoesNotExist() throws Exception {
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.delete(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS + "/99");
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	@DatabaseSetup(FILE_PATH + DELETE + JWTTOKEN + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + DELETE + JWTTOKEN
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void deleteAppointmentMemberWithUserId() throws Exception {

		this.setMockRestServiceServer(AUTHUSER_1);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.delete(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS + "/1");
		requestBuilder.cookie(cookieBuilder.createJWTCookie(JWTTOKEN_VALUE, false));
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isNoContent())
				.andExpect(MockMvcResultMatchers.cookie().maxAge(COOKIE_JWTTOKEN, 60000));
	}

	@Test
	@DatabaseSetup(FILE_PATH + DELETE + JWTTOKEN + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + DELETE + JWTTOKEN
			+ FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void deleteAppointmentMemberWithUserIdShouldFailNoAccess() throws Exception {
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.delete(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS + "/1");
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@DatabaseSetup(FILE_PATH + DELETE + JWTTOKEN + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + DELETE + JWTTOKEN
			+ FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void deleteAppointmentMemberWithUserIdShouldFailHasNoAccess() throws Exception {

		this.setMockRestServiceServer(AUTHUSER_2);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.delete(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS + "/1");
		requestBuilder.cookie(cookieBuilder.createJWTCookie(JWTTOKEN_VALUE, false));
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.cookie().maxAge(COOKIE_JWTTOKEN, 60000));
	}

	@Test
	@DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateAppointmentMember() throws Exception {

		AppointmentMemberDTO appointmentMemberDTO = this.createSimpleUpdatedAppointmentMember();

		String json = gson.toJson(appointmentMemberDTO);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.put(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS + "/1");
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE
			+ FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateAppointmentMemberShouldFailDoesNotExist() throws Exception {

		AppointmentMemberDTO appointmentMemberDTO = this.createSimpleUpdatedAppointmentMember();

		String json = gson.toJson(appointmentMemberDTO);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.put(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS + "/99");
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	@DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE
			+ FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateAppointmentMemberShouldFailTooManyDecisions() throws Exception {

		AppointmentMemberDTO appointmentMemberDTO = this.createSimpleAppointmentMember();
		appointmentMemberDTO.setId(1);

		List<AppointmentDecisionDTO> appointmentDecisions = appointmentMemberDTO.getAppointmentDecisions();

		appointmentDecisions.add(new AppointmentDecisionDTO(3, 1, 1, null));

		appointmentMemberDTO.setAppointmentDecisions(appointmentDecisions);

		String json = gson.toJson(appointmentMemberDTO);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.put(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS + "/1");
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	@DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE
			+ FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateAppointmentMemberShouldFailWrongDecisionType() throws Exception {

		AppointmentMemberDTO appointmentMemberDTO = this.createSimpleAppointmentMember();
		appointmentMemberDTO.setId(1);

		List<AppointmentDecisionDTO> appointmentDecisions = appointmentMemberDTO.getAppointmentDecisions();

		appointmentDecisions.set(0, new AppointmentDecisionDTO(1, 2, null, 15));
		appointmentDecisions.set(0, new AppointmentDecisionDTO(2, 2, null, 50));

		appointmentMemberDTO.setAppointmentDecisions(appointmentDecisions);

		String json = gson.toJson(appointmentMemberDTO);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.put(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS + "/1");
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	@DatabaseSetup(FILE_PATH + UPDATE + PASSWORD + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE + PASSWORD
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateAppointmentMemberWithPassword() throws Exception {

		AppointmentMemberDTO appointmentMemberDTO = this.createSimpleUpdatedAppointmentMember();

		String json = gson.toJson(appointmentMemberDTO);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.put(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS + "/1");
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());
		requestBuilder.header(HEADER_ACCESS_TOKEN, ACCESS_TOKEN_PASSWORD);

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@DatabaseSetup(FILE_PATH + UPDATE + PASSWORD + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE + PASSWORD
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateAppointmentMemberWithAdminPassword() throws Exception {

		AppointmentMemberDTO appointmentMemberDTO = this.createSimpleUpdatedAppointmentMember();

		String json = gson.toJson(appointmentMemberDTO);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.put(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS + "/1");
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());
		requestBuilder.header(HEADER_ACCESS_TOKEN, ACCESS_TOKEN_ADMIN_PASSWORD);

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@DatabaseSetup(FILE_PATH + UPDATE + PASSWORD + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE + PASSWORD
			+ FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateAppointmentMemberWithWrongPasswordShouldFail() throws Exception {

		AppointmentMemberDTO appointmentMemberDTO = this.createSimpleUpdatedAppointmentMember();

		String json = gson.toJson(appointmentMemberDTO);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.put(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS + "/1");
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());
		requestBuilder.header(HEADER_ACCESS_TOKEN, ACCESS_TOKEN_WRONG_PASSWORD);

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@DatabaseSetup(FILE_PATH + UPDATE + JWTTOKEN + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE + JWTTOKEN
			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateAppointmentMemberWithJWTToken() throws Exception {

		AppointmentMemberDTO appointmentMemberDTO = this.createSimpleUpdatedAppointmentMember();

		this.setMockRestServiceServer(AUTHUSER_1);

		String json = gson.toJson(appointmentMemberDTO);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.put(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS + "/1");
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());
		requestBuilder.cookie(cookieBuilder.createJWTCookie(JWTTOKEN_VALUE, false));

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.cookie().maxAge(COOKIE_JWTTOKEN, 60000));
	}

	@Test
	@DatabaseSetup(FILE_PATH + UPDATE + JWTTOKEN + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + UPDATE + JWTTOKEN
			+ FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void updateAppointmentMemberWithJWTTokenHasNoAccess() throws Exception {

		AppointmentMemberDTO appointmentMemberDTO = this.createSimpleUpdatedAppointmentMember();

		this.setMockRestServiceServer(AUTHUSER_2);

		String json = gson.toJson(appointmentMemberDTO);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.put(VERSION + APPOINTMENTREQUESTS + "/1" + APPOINTMENTMEMBERS + "/1");
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());
		requestBuilder.cookie(cookieBuilder.createJWTCookie(JWTTOKEN_VALUE, false));

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.cookie().maxAge(COOKIE_JWTTOKEN, 60000));
	}

	private AppointmentMemberDTO createSimpleAppointmentMember() {
		AppointmentMemberDTO appointmentMemberDTO = new AppointmentMemberDTO();
		appointmentMemberDTO.setAppointmentRequestId(1);
		appointmentMemberDTO.setName("IKS");

		List<AppointmentDecisionDTO> appointmentDecisions = new ArrayList<>();

		appointmentDecisions.add(new AppointmentDecisionDTO(1, 1, 0, null));
		appointmentDecisions.add(new AppointmentDecisionDTO(2, 1, 1, null));

		appointmentMemberDTO.setAppointmentDecisions(appointmentDecisions);
		return appointmentMemberDTO;
	}

	private AppointmentMemberDTO createSimpleUpdatedAppointmentMember() {
		AppointmentMemberDTO appointmentMemberDTO = this.createSimpleAppointmentMember();
		appointmentMemberDTO.setId(1);

		List<AppointmentDecisionDTO> appointmentDecisions = appointmentMemberDTO.getAppointmentDecisions();

		appointmentDecisions.get(0).setId(1);
		appointmentDecisions.get(0).setAppointmentMemberId(1);

		appointmentDecisions.get(1).setId(2);
		appointmentDecisions.get(1).setAppointmentMemberId(1);

		appointmentMemberDTO.setAppointmentDecisions(appointmentDecisions);
		return appointmentMemberDTO;
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
