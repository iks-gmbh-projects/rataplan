package de.iks.rataplan.controller;

import static de.iks.rataplan.testutils.ITConstants.APPOINTMENTREQUESTS;
import static de.iks.rataplan.testutils.ITConstants.AUTHUSER_1;
import static de.iks.rataplan.testutils.ITConstants.AUTHUSER_2;
import static de.iks.rataplan.testutils.ITConstants.AUTHUSER_3;
import static de.iks.rataplan.testutils.ITConstants.AUTH_SERVICE_URL;
import static de.iks.rataplan.testutils.ITConstants.COOKIE_JWTTOKEN;
import static de.iks.rataplan.testutils.ITConstants.COOKIE_MAX_AGE;
import static de.iks.rataplan.testutils.ITConstants.CREATIONS;
import static de.iks.rataplan.testutils.ITConstants.FILE_EXPECTED;
import static de.iks.rataplan.testutils.ITConstants.FILE_INITIAL;
import static de.iks.rataplan.testutils.ITConstants.FRONTENDUSER_1;
import static de.iks.rataplan.testutils.ITConstants.FRONTENDUSER_1_NEW;
import static de.iks.rataplan.testutils.ITConstants.FRONTENDUSER_2;
import static de.iks.rataplan.testutils.ITConstants.LOGIN;
import static de.iks.rataplan.testutils.ITConstants.LOGOUT;
import static de.iks.rataplan.testutils.ITConstants.JWTTOKEN_VALUE;
import static de.iks.rataplan.testutils.ITConstants.PARTICIPATIONS;
import static de.iks.rataplan.testutils.ITConstants.PATH;
import static de.iks.rataplan.testutils.ITConstants.PROFILE;
import static de.iks.rataplan.testutils.ITConstants.REGISTER;
import static de.iks.rataplan.testutils.ITConstants.USERS;
import static de.iks.rataplan.testutils.ITConstants.VERSION;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
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
import org.springframework.test.web.servlet.MvcResult;
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
import de.iks.rataplan.dto.AppointmentRequestDTO;
import de.iks.rataplan.utils.CookieBuilder;

@ActiveProfiles("integration")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class, IntegrationConfig.class })
@WebAppConfiguration
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class UserIT {

	private static final String FILE_PATH = PATH + USERS;

	private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
	private MockMvc mockMvc;
	private MockRestServiceServer mockRestServiceServer;

	@Autowired
	private RestOperations restOperations;

	@Resource
	private WebApplicationContext webApplicationContext;

	@Autowired
	private CookieBuilder cookieBuilder;

	@Autowired
	private ModelMapper modelMapper;

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
	@DatabaseSetup(FILE_PATH + REGISTER + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + REGISTER + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void registerUser() throws Exception {

		this.setMockRestServiceServer(AUTHUSER_1, REGISTER, HttpMethod.POST);

		String json = gson.toJson(FRONTENDUSER_1_NEW);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(VERSION + USERS + REGISTER);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.cookie().maxAge(COOKIE_JWTTOKEN, COOKIE_MAX_AGE));
	}

	@Test
	@DatabaseSetup(FILE_PATH + REGISTER + FILE_EXPECTED)
	@ExpectedDatabase(value = FILE_PATH + REGISTER + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void registerSecondUserFailsAuthUserIdAlreadyExists() throws Exception {

		this.setMockRestServiceServer(AUTHUSER_1, REGISTER, HttpMethod.POST);

		String json = gson.toJson(FRONTENDUSER_1_NEW);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(VERSION + USERS + REGISTER);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	@DatabaseSetup(FILE_PATH + LOGIN + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + LOGIN + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void loginUser() throws Exception {

		this.setMockRestServiceServer(AUTHUSER_1, LOGIN, HttpMethod.POST);

		String json = gson.toJson(FRONTENDUSER_1_NEW);

		String expectedJson = gson.toJson(FRONTENDUSER_1);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(VERSION + USERS + LOGIN);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.cookie().maxAge(COOKIE_JWTTOKEN, COOKIE_MAX_AGE))
				.andExpect(MockMvcResultMatchers.content().json(expectedJson));
	}

	@Test
	@DatabaseSetup(FILE_PATH + LOGIN + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + LOGIN + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void loginUserCreateBackendUser() throws Exception {

		this.setMockRestServiceServer(AUTHUSER_2, LOGIN, HttpMethod.POST);

		String json = gson.toJson(FRONTENDUSER_1_NEW);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(VERSION + USERS + LOGIN);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.cookie().maxAge(COOKIE_JWTTOKEN, COOKIE_MAX_AGE));
	}

	@Test
	public void logout() throws Exception {

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(VERSION + USERS + LOGOUT);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.cookie().maxAge(COOKIE_JWTTOKEN, 0));
	}

	@Test
	@DatabaseSetup(FILE_PATH + PROFILE + FILE_EXPECTED)
	public void getUserData() throws Exception {

		this.setMockRestServiceServer(AUTHUSER_1, PROFILE, HttpMethod.GET);

		String expectedJson = gson.toJson(FRONTENDUSER_1);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(VERSION + USERS + PROFILE);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.cookie(cookieBuilder.createJWTCookie(JWTTOKEN_VALUE, false));

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.cookie().maxAge(COOKIE_JWTTOKEN, 60000))
				.andExpect(MockMvcResultMatchers.content().json(expectedJson));
	}

	@Test
	@DatabaseSetup(FILE_PATH + PROFILE + FILE_EXPECTED)
	public void getUserData2() throws Exception {

		this.setMockRestServiceServer(AUTHUSER_2, PROFILE, HttpMethod.GET);

		String expectedJson = gson.toJson(FRONTENDUSER_2);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(VERSION + USERS + PROFILE);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.cookie(cookieBuilder.createJWTCookie(JWTTOKEN_VALUE, false));

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.cookie().maxAge(COOKIE_JWTTOKEN, 60000))
				.andExpect(MockMvcResultMatchers.content().json(expectedJson));
	}

	@Test
	@DatabaseSetup(FILE_PATH + APPOINTMENTREQUESTS + CREATIONS + FILE_INITIAL)
	public void getAppointmentRequestsCreatedByUser1() throws Exception {

		this.setMockRestServiceServer(AUTHUSER_1, PROFILE, HttpMethod.GET);

		List<AppointmentRequestDTO> appointmentRequestsDTO = this
				.getAppointmentRequests(VERSION + USERS + APPOINTMENTREQUESTS + CREATIONS);

		assertEquals(appointmentRequestsDTO.size(), 1);
		assertEquals(appointmentRequestsDTO.get(0).getId().intValue(), 1);
	}

	@Test
	@DatabaseSetup(FILE_PATH + APPOINTMENTREQUESTS + CREATIONS + FILE_INITIAL)
	public void getAppointmentRequestsCreatedByUser2() throws Exception {

		this.setMockRestServiceServer(AUTHUSER_2, PROFILE, HttpMethod.GET);

		List<AppointmentRequestDTO> appointmentRequestsDTO = this
				.getAppointmentRequests(VERSION + USERS + APPOINTMENTREQUESTS + CREATIONS);

		assertEquals(appointmentRequestsDTO.size(), 2);
	}

	@Test
	@DatabaseSetup(FILE_PATH + APPOINTMENTREQUESTS + CREATIONS + FILE_INITIAL)
	public void getAppointmentRequestsCreatedByUser3() throws Exception {

		this.setMockRestServiceServer(AUTHUSER_3, PROFILE, HttpMethod.GET);

		List<AppointmentRequestDTO> appointmentRequestsDTO = this
				.getAppointmentRequests(VERSION + USERS + APPOINTMENTREQUESTS + CREATIONS);

		assertEquals(appointmentRequestsDTO.size(), 0);
	}

	@Test
	@DatabaseSetup(FILE_PATH + APPOINTMENTREQUESTS + PARTICIPATIONS + FILE_INITIAL)
	public void getAppointmentRequestsWhereUser1participates() throws Exception {

		this.setMockRestServiceServer(AUTHUSER_1, PROFILE, HttpMethod.GET);

		List<AppointmentRequestDTO> appointmentRequestsDTO = this
				.getAppointmentRequests(VERSION + USERS + APPOINTMENTREQUESTS + PARTICIPATIONS);

		assertEquals(appointmentRequestsDTO.size(), 2);
	}

	@Test
	@DatabaseSetup(FILE_PATH + APPOINTMENTREQUESTS + PARTICIPATIONS + FILE_INITIAL)
	public void getAppointmentRequestsWhereUser2participates() throws Exception {

		this.setMockRestServiceServer(AUTHUSER_2, PROFILE, HttpMethod.GET);

		List<AppointmentRequestDTO> appointmentRequestsDTO = this
				.getAppointmentRequests(VERSION + USERS + APPOINTMENTREQUESTS + PARTICIPATIONS);

		assertEquals(appointmentRequestsDTO.size(), 0);
	}

	private void setMockRestServiceServer(AuthUser authUser, String path, HttpMethod httpMethod) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(COOKIE_JWTTOKEN, "value of returned token");

		mockRestServiceServer.expect(MockRestRequestMatchers.requestTo(AUTH_SERVICE_URL + "/users" + path))
				.andExpect(MockRestRequestMatchers.method(httpMethod))
				.andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
						.body(gson.toJson(authUser)).headers(responseHeaders));
	}

	private List<AppointmentRequestDTO> getAppointmentRequests(String path) throws Exception {
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(path);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.cookie(cookieBuilder.createJWTCookie(JWTTOKEN_VALUE, false));

		MvcResult mvcResult = this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.cookie().maxAge(COOKIE_JWTTOKEN, 60000)).andReturn();

		String json = mvcResult.getResponse().getContentAsString();
		List<?> responseList = gson.fromJson(json, List.class);

		List<AppointmentRequestDTO> appointmentRequestsDTO = new ArrayList<>();

		for (Object object : responseList) {
			appointmentRequestsDTO.add(modelMapper.map(object, AppointmentRequestDTO.class));
		}
		return appointmentRequestsDTO;
	}

}
