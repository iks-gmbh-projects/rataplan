//package de.iks.rataplan.controller;
//
//import static de.iks.rataplan.testutils.ITConstants.ACCESS_TOKEN_ADMIN_PASSWORD;
//import static de.iks.rataplan.testutils.ITConstants.ACCESS_TOKEN_PASSWORD;
//import static de.iks.rataplan.testutils.ITConstants.ACCESS_TOKEN_WRONG_PASSWORD;
//import static de.iks.rataplan.testutils.ITConstants.APPOINTMENTREQUESTS;
//import static de.iks.rataplan.testutils.ITConstants.AUTHUSER_1;
//import static de.iks.rataplan.testutils.ITConstants.AUTHUSER_2;
//import static de.iks.rataplan.testutils.ITConstants.AUTHUSER_3;
//import static de.iks.rataplan.testutils.ITConstants.AUTH_SERVICE_URL;
//import static de.iks.rataplan.testutils.ITConstants.COOKIE_JWTTOKEN;
//import static de.iks.rataplan.testutils.ITConstants.CREATE;
//import static de.iks.rataplan.testutils.ITConstants.DATE_2050_10_10;
//import static de.iks.rataplan.testutils.ITConstants.EDIT;
//import static de.iks.rataplan.testutils.ITConstants.FILE_EMPTY_DB;
//import static de.iks.rataplan.testutils.ITConstants.FILE_EXPECTED;
//import static de.iks.rataplan.testutils.ITConstants.FILE_INITIAL;
//import static de.iks.rataplan.testutils.ITConstants.GET;
//import static de.iks.rataplan.testutils.ITConstants.HEADER_ACCESS_TOKEN;
//import static de.iks.rataplan.testutils.ITConstants.IKS_MAIL;
//import static de.iks.rataplan.testutils.ITConstants.JWTTOKEN;
//import static de.iks.rataplan.testutils.ITConstants.JWTTOKEN_VALUE;
//import static de.iks.rataplan.testutils.ITConstants.PASSWORD;
//import static de.iks.rataplan.testutils.ITConstants.PATH;
//import static de.iks.rataplan.testutils.ITConstants.UPDATE;
//import static de.iks.rataplan.testutils.ITConstants.VERSION;
//
//import java.sql.Date;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.annotation.Resource;
//import javax.transaction.Transactional;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.TestExecutionListeners;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
//import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
//import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.test.web.client.MockRestServiceServer;
//import org.springframework.test.web.client.match.MockRestRequestMatchers;
//import org.springframework.test.web.client.response.MockRestResponseCreators;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.client.RestOperations;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.context.WebApplicationContext;
//
//import com.github.springtestdbunit.DbUnitTestExecutionListener;
//import com.github.springtestdbunit.annotation.DatabaseSetup;
//import com.github.springtestdbunit.annotation.ExpectedDatabase;
//import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//
//import de.iks.rataplan.config.AppConfig;
//import de.iks.rataplan.config.IntegrationConfig;
//import de.iks.rataplan.domain.AppointmentConfig;
//import de.iks.rataplan.domain.AppointmentRequestConfig;
//import de.iks.rataplan.domain.AuthUser;
//import de.iks.rataplan.domain.DecisionType;
//import de.iks.rataplan.dto.AppointmentDTO;
//import de.iks.rataplan.dto.AppointmentRequestDTO;
//import de.iks.rataplan.utils.CookieBuilder;
//
//@ActiveProfiles("integration")
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = { AppConfig.class, IntegrationConfig.class })
//@WebAppConfiguration
//@Transactional
//@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
//		TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
//public class AppointmentRequestIT {
//
//	private static final String FILE_PATH = PATH + APPOINTMENTREQUESTS;
//
//	private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
//	private MockMvc mockMvc;
//	private MockRestServiceServer mockRestServiceServer;
//
//	@Autowired
//	private CookieBuilder cookieBuilder;
//
//	@Autowired
//	private RestOperations restOperations;
//
//	@Resource
//	private WebApplicationContext webApplicationContext;
//
//	@Before
//	public void initMocks() {
//		MockitoAnnotations.initMocks(this);
//	}
//
//	@Before
//	public void setUp() {
//		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//		mockRestServiceServer = MockRestServiceServer.createServer((RestTemplate) restOperations);
//	}
//
//	@Test
//	public void handleOptions() throws Exception {
//		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.options(VERSION + "/");
//		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk());
//	}
//
//	@Test
//	@DatabaseSetup(FILE_PATH + GET + FILE_INITIAL)
//	public void getOneAppointmentRequestById() throws Exception {
//		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(VERSION + APPOINTMENTREQUESTS + "/2");
//		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk());
//	}
//
//	@Test
//	@DatabaseSetup(FILE_PATH + GET + FILE_INITIAL)
//	public void getOneAppointmentRequestByIdShouldFailDoesNotExist() throws Exception {
//		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(VERSION + APPOINTMENTREQUESTS + "/3");
//		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isNotFound());
//	}
//
//	@Test
//	@DatabaseSetup(FILE_PATH + GET + FILE_INITIAL)
//	public void getOneAppointmentRequestByIdWithPassword() throws Exception {
//		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(VERSION + APPOINTMENTREQUESTS + "/1");
//		requestBuilder.header(HEADER_ACCESS_TOKEN, ACCESS_TOKEN_PASSWORD);
//		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk());
//	}
//
//	@Test
//	@DatabaseSetup(FILE_PATH + GET + FILE_INITIAL)
//	public void getOneAppointmentRequestByIdWithWrongPasswordShouldFail() throws Exception {
//		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(VERSION + APPOINTMENTREQUESTS + "/1");
//		requestBuilder.header(HEADER_ACCESS_TOKEN, ACCESS_TOKEN_WRONG_PASSWORD);
//		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isForbidden());
//	}
//
//	@Test
//	@DatabaseSetup(FILE_PATH + GET + FILE_INITIAL)
//	public void getOneAppointmentRequestByIdWithAdminPassword() throws Exception {
//		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(VERSION + APPOINTMENTREQUESTS + "/1");
//		requestBuilder.header(HEADER_ACCESS_TOKEN, ACCESS_TOKEN_ADMIN_PASSWORD);
//		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk());
//	}
//
//	@Test
//	@DatabaseSetup(FILE_PATH + GET + FILE_INITIAL)
//	public void getOneAppointmentRequestByIdWithWrongAdminPasswordShouldFail() throws Exception {
//		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(VERSION + APPOINTMENTREQUESTS + "/1");
//		requestBuilder.header(HEADER_ACCESS_TOKEN, ACCESS_TOKEN_WRONG_PASSWORD);
//		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isForbidden());
//	}
//
//	@Test
//	@DatabaseSetup(FILE_PATH + GET + FILE_INITIAL)
//	public void getOneAppointmentRequestByIdWithJWTToken() throws Exception {
//		this.setMockRestServiceServer(AUTHUSER_1);
//
//		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(VERSION + APPOINTMENTREQUESTS + "/1");
//		requestBuilder.cookie(cookieBuilder.createJWTCookie(JWTTOKEN_VALUE, false));
//
//		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk())
//				.andExpect(MockMvcResultMatchers.cookie().maxAge(COOKIE_JWTTOKEN, 60000));
//	}
//
//	@Test
//	@DatabaseSetup(FILE_PATH + GET + FILE_INITIAL)
//	public void getOneAppointmentRequestByIdWithJWTTokenShouldFailNoAccess() throws Exception {
//		this.setMockRestServiceServer(AUTHUSER_2);
//
//		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(VERSION + APPOINTMENTREQUESTS + "/1");
//		requestBuilder.cookie(cookieBuilder.createJWTCookie(JWTTOKEN_VALUE, false));
//
//		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isForbidden())
//				.andExpect(MockMvcResultMatchers.cookie().maxAge(COOKIE_JWTTOKEN, 60000));
//	}
//
//	@Test
//	@DatabaseSetup(FILE_EMPTY_DB)
//	@ExpectedDatabase(value = FILE_PATH + CREATE
//			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
//	public void createAppointmentRequest() throws Exception {
//
//		AppointmentRequestDTO appointmentRequestDTO = this.createSimpleAppointmentRequestDTO();
//
//		String json = gson.toJson(appointmentRequestDTO);
//
//		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(VERSION + APPOINTMENTREQUESTS);
//		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
//		requestBuilder.content(json.getBytes());
//
//		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isCreated());
//	}
//
//	@Test
//	@DatabaseSetup(FILE_EMPTY_DB)
//	@ExpectedDatabase(value = FILE_PATH + CREATE + PASSWORD
//			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
//	public void createAppointmentRequestWithPasswords() throws Exception {
//
//		AppointmentRequestDTO appointmentRequestDTO = this.createSimpleAppointmentRequestDTO();
//		appointmentRequestDTO.getAppointmentRequestConfig().setAdminPassword(ACCESS_TOKEN_ADMIN_PASSWORD);
//		appointmentRequestDTO.getAppointmentRequestConfig().setPassword(ACCESS_TOKEN_PASSWORD);
//
//		String json = gson.toJson(appointmentRequestDTO);
//
//		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(VERSION + APPOINTMENTREQUESTS);
//		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
//		requestBuilder.content(json.getBytes());
//
//		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isCreated());
//	}
//
//	@Test
//	@DatabaseSetup(FILE_PATH + CREATE + JWTTOKEN + FILE_INITIAL)
//	@ExpectedDatabase(value = FILE_PATH + CREATE + JWTTOKEN
//			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
//	public void createAppointmentRequestWithJWTToken() throws Exception {
//
//		this.setMockRestServiceServer(AUTHUSER_1);
//
//		AppointmentRequestDTO appointmentRequestDTO = this.createSimpleAppointmentRequestDTO();
//
//		String json = gson.toJson(appointmentRequestDTO);
//
//		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(VERSION + APPOINTMENTREQUESTS);
//		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
//		requestBuilder.content(json.getBytes());
//		requestBuilder.cookie(cookieBuilder.createJWTCookie(JWTTOKEN_VALUE, false));
//
//		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isCreated())
//				.andExpect(MockMvcResultMatchers.cookie().maxAge(COOKIE_JWTTOKEN, 60000));
//	}
//
//	@Test
//	@DatabaseSetup(FILE_EMPTY_DB)
//	@ExpectedDatabase(value = FILE_EMPTY_DB, assertionMode = DatabaseAssertionMode.NON_STRICT)
//	public void createAppointmentRequestShouldFailWrongAppointment() throws Exception {
//
//		AppointmentRequestDTO appointmentRequestDTO = this.createSimpleAppointmentRequestDTO();
//
//		appointmentRequestDTO.getAppointments().get(appointmentRequestDTO.getAppointments().size() - 1)
//				.setUrl("this-wont-work.com");
//
//		String json = gson.toJson(appointmentRequestDTO);
//
//		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(VERSION + APPOINTMENTREQUESTS);
//		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
//		requestBuilder.content(json.getBytes());
//
//		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isBadRequest());
//	}
//
//	@Test
//	@DatabaseSetup(FILE_PATH + GET + EDIT + FILE_INITIAL)
//	@ExpectedDatabase(value = FILE_PATH + GET + EDIT + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
//	public void getAppointmentRequestForEditWithAdminPassword() throws Exception {
//
//		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(VERSION + APPOINTMENTREQUESTS + "/1" + EDIT);
//		requestBuilder.header(HEADER_ACCESS_TOKEN, ACCESS_TOKEN_ADMIN_PASSWORD);
//		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk());
//	}
//
//	@Test
//	@DatabaseSetup(FILE_PATH + GET + EDIT + FILE_INITIAL)
//	@ExpectedDatabase(value = FILE_PATH + GET + EDIT + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
//	public void getAppointmentRequestForEditShouldFailShowPassword() throws Exception {
//
//		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(VERSION + APPOINTMENTREQUESTS + "/1" + EDIT);
//		requestBuilder.header(HEADER_ACCESS_TOKEN, ACCESS_TOKEN_PASSWORD);
//		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isForbidden());
//	}
//
//	@Test
//	@DatabaseSetup(FILE_PATH + GET + EDIT + FILE_INITIAL)
//	@ExpectedDatabase(value = FILE_PATH + GET + EDIT + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
//	public void getAppointmentRequestForEditShouldFailWrongPassword() throws Exception {
//
//		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(VERSION + APPOINTMENTREQUESTS + "/1" + EDIT);
//		requestBuilder.header(HEADER_ACCESS_TOKEN, ACCESS_TOKEN_WRONG_PASSWORD);
//		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isForbidden());
//	}
//
//	@Test
//	@DatabaseSetup(FILE_PATH + GET + EDIT + FILE_INITIAL)
//	@ExpectedDatabase(value = FILE_PATH + GET + EDIT + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
//	public void getAppointmentRequestForEditWithJWT() throws Exception {
//
//		this.setMockRestServiceServer(AUTHUSER_2);
//
//		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(VERSION + APPOINTMENTREQUESTS + "/1" + EDIT);
//		requestBuilder.cookie(cookieBuilder.createJWTCookie(JWTTOKEN_VALUE, false));
//		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk());
//	}
//
//	@Test
//	@DatabaseSetup(FILE_PATH + GET + EDIT + FILE_INITIAL)
//	@ExpectedDatabase(value = FILE_PATH + GET + EDIT + FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
//	public void getAppointmentRequestForEditWithJWTAddEditAccess() throws Exception {
//
//		this.setMockRestServiceServer(AUTHUSER_1);
//
//		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(VERSION + APPOINTMENTREQUESTS + "/1" + EDIT);
//		requestBuilder.header(HEADER_ACCESS_TOKEN, ACCESS_TOKEN_ADMIN_PASSWORD);
//		requestBuilder.cookie(cookieBuilder.createJWTCookie(JWTTOKEN_VALUE, false));
//		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk());
//	}
//
//	@Test
//	@DatabaseSetup(FILE_PATH + GET + EDIT + FILE_INITIAL)
//	@ExpectedDatabase(value = FILE_PATH + GET + EDIT + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
//	public void getAppointmentRequestForEditWithJWTShouldFailWrongPassword() throws Exception {
//
//		this.setMockRestServiceServer(AUTHUSER_1);
//
//		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(VERSION + APPOINTMENTREQUESTS + "/1" + EDIT);
//		requestBuilder.header(HEADER_ACCESS_TOKEN, ACCESS_TOKEN_PASSWORD);
//		requestBuilder.cookie(cookieBuilder.createJWTCookie(JWTTOKEN_VALUE, false));
//		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isForbidden());
//	}
//
//	@Test
//	@DatabaseSetup(FILE_PATH + GET + EDIT + FILE_INITIAL)
//	@ExpectedDatabase(value = FILE_PATH + GET + EDIT + "/expected_new.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
//	public void getAppointmentRequestForEditWithJWTAddBackendUserAccess() throws Exception {
//
//		this.setMockRestServiceServer(AUTHUSER_3);
//
//		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(VERSION + APPOINTMENTREQUESTS + "/1" + EDIT);
//		requestBuilder.header(HEADER_ACCESS_TOKEN, ACCESS_TOKEN_ADMIN_PASSWORD);
//		requestBuilder.cookie(cookieBuilder.createJWTCookie(JWTTOKEN_VALUE, false));
//		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk());
//	}
//
//	@Test
//	@DatabaseSetup(FILE_PATH + GET + EDIT + FILE_INITIAL)
//	@ExpectedDatabase(value = FILE_PATH + GET + EDIT + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
//	public void getAppointmentRequestForEditWithJWTShouldFailNoAccess() throws Exception {
//
//		this.setMockRestServiceServer(AUTHUSER_1);
//
//		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(VERSION + APPOINTMENTREQUESTS + "/1" + EDIT);
//		requestBuilder.cookie(cookieBuilder.createJWTCookie(JWTTOKEN_VALUE, false));
//		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isForbidden());
//	}
//
//	@Test
//	@DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
//	@ExpectedDatabase(value = FILE_PATH + UPDATE
//			+ FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
//	public void updateAppointmentRequest() throws Exception {
//
//		AppointmentRequestDTO appointmentRequestDTO = this.createSimpleAppointmentRequestDTOForUpdateNoPW();
//
//		String json = gson.toJson(appointmentRequestDTO);
//
//		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(VERSION + APPOINTMENTREQUESTS + "/1");
//		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
//		requestBuilder.content(json.getBytes());
//		requestBuilder.header(HEADER_ACCESS_TOKEN, ACCESS_TOKEN_ADMIN_PASSWORD);
//
//		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isAccepted());
//	}
//
//	@Test
//	@DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
//	@ExpectedDatabase(value = FILE_PATH + UPDATE
//			+ FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
//	public void updateAppointmentRequestWrongAdminPassword() throws Exception {
//
//		AppointmentRequestDTO appointmentRequestDTO = this.createSimpleAppointmentRequestDTOForUpdate();
//
//		String json = gson.toJson(appointmentRequestDTO);
//
//		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(VERSION + APPOINTMENTREQUESTS + "/1");
//		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
//		requestBuilder.content(json.getBytes());
//		requestBuilder.header(HEADER_ACCESS_TOKEN, ACCESS_TOKEN_WRONG_PASSWORD);
//
//		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isForbidden());
//	}
//
//	@Test
//	@DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
//	@ExpectedDatabase(value = FILE_PATH + UPDATE
//			+ FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
//	public void updateAppointmentRequestShouldFailWrongAppointment() throws Exception {
//
//		AppointmentRequestDTO appointmentRequestDTO = this.createSimpleAppointmentRequestDTOForUpdate();
//
//		appointmentRequestDTO.getAppointments().get(appointmentRequestDTO.getAppointments().size() - 1)
//				.setUrl("this-wont-work.com");
//
//		String json = gson.toJson(appointmentRequestDTO);
//
//		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(VERSION + APPOINTMENTREQUESTS + "/1");
//		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
//		requestBuilder.content(json.getBytes());
//
//		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isForbidden());
//	}
//
//	@Test
//	@DatabaseSetup(FILE_PATH + UPDATE + FILE_INITIAL)
//	@ExpectedDatabase(value = FILE_PATH + UPDATE
//			+ FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
//	public void updateAppointmentRequestShouldFailIsNotFound() throws Exception {
//
//		AppointmentRequestDTO appointmentRequestDTO = this.createSimpleAppointmentRequestDTO();
//
//		String json = gson.toJson(appointmentRequestDTO);
//
//		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(VERSION + APPOINTMENTREQUESTS + "/3");
//		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
//		requestBuilder.content(json.getBytes());
//
//		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isNotFound());
//	}
//
//	private AppointmentRequestDTO createSimpleAppointmentRequestDTOForUpdate() {
//		AppointmentRequestDTO appointmentRequestDTO = this.createSimpleAppointmentRequestDTO();
//
//		appointmentRequestDTO.getAppointmentRequestConfig().setId(1);
//		appointmentRequestDTO.getAppointmentRequestConfig().setPassword(ACCESS_TOKEN_PASSWORD);
//		appointmentRequestDTO.getAppointmentRequestConfig().setAdminPassword(ACCESS_TOKEN_ADMIN_PASSWORD);
//		appointmentRequestDTO.setId(1);
//		appointmentRequestDTO.getAppointments().get(0).setId(1);
//		appointmentRequestDTO.getAppointments().get(0).setRequestId(appointmentRequestDTO.getId());
//		appointmentRequestDTO.getAppointments().get(1).setRequestId(appointmentRequestDTO.getId());
//
//		return appointmentRequestDTO;
//	}
//
//	private AppointmentRequestDTO createSimpleAppointmentRequestDTOForUpdateNoPW() {
//		AppointmentRequestDTO appointmentRequestDTO = this.createSimpleAppointmentRequestDTO();
//
//		appointmentRequestDTO.getAppointmentRequestConfig().setId(1);
//		appointmentRequestDTO.setId(1);
//		appointmentRequestDTO.getAppointments().get(0).setId(1);
//		appointmentRequestDTO.getAppointments().get(0).setRequestId(appointmentRequestDTO.getId());
//		appointmentRequestDTO.getAppointments().remove(1);
//
//		return appointmentRequestDTO;
//	}
//
//	private AppointmentRequestDTO createSimpleAppointmentRequestDTO() {
//
//		AppointmentRequestConfig config = new AppointmentRequestConfig(null,
//				new AppointmentConfig(true, false, false, false, false, false), DecisionType.DEFAULT, null, null);
//
//		AppointmentRequestDTO appointmentRequestDTO = new AppointmentRequestDTO("Coding Dojo", "Fun with code",
//				new Date(DATE_2050_10_10), IKS_MAIL, config);
//
//		List<AppointmentDTO> appointmentDTOs = new ArrayList<>();
//		appointmentDTOs.add(new AppointmentDTO("iks Hilden"));
//		appointmentDTOs.add(new AppointmentDTO("Solingen"));
//
//		appointmentRequestDTO.setAppointments(appointmentDTOs);
//
//		return appointmentRequestDTO;
//	}
//
//	private void setMockRestServiceServer(AuthUser authUser) {
//		HttpHeaders responseHeaders = new HttpHeaders();
//		responseHeaders.set("jwttoken", "value of returned token");
//
//		mockRestServiceServer.expect(MockRestRequestMatchers.requestTo(AUTH_SERVICE_URL + "/users/profile"))
//				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
//				.andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
//						.body(gson.toJson(authUser)).headers(responseHeaders));
//	}
//
//}
