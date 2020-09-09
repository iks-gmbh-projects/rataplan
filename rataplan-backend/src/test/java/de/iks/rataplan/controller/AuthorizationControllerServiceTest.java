package de.iks.rataplan.controller;

import static de.iks.rataplan.testutils.TestConstants.ACCESS_TOKEN_ADMIN_PASSWORD;
import static de.iks.rataplan.testutils.TestConstants.ACCESS_TOKEN_PASSWORD;
import static de.iks.rataplan.testutils.TestConstants.ACCESS_TOKEN_WRONG_PASSWORD;
import static de.iks.rataplan.testutils.TestConstants.AUTHORIZATION;
import static de.iks.rataplan.testutils.TestConstants.AUTHUSER_1;
import static de.iks.rataplan.testutils.TestConstants.AUTHUSER_2;
import static de.iks.rataplan.testutils.TestConstants.AUTHUSER_3;
import static de.iks.rataplan.testutils.TestConstants.AUTH_SERVICE_URL;
import static de.iks.rataplan.testutils.TestConstants.CONTROLLERSERVICE;
import static de.iks.rataplan.testutils.TestConstants.ENTERED_JWTTOKEN;
import static de.iks.rataplan.testutils.TestConstants.FILE_INITIAL;
import static de.iks.rataplan.testutils.TestConstants.PATH;
import static org.junit.Assert.assertEquals;

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
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.iks.rataplan.config.AppConfig;
import de.iks.rataplan.config.TestConfig;
import de.iks.rataplan.domain.AppointmentRequest;
import de.iks.rataplan.domain.AuthUser;
import de.iks.rataplan.exceptions.ForbiddenException;
import de.iks.rataplan.exceptions.ResourceNotFoundException;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class, TestConfig.class })
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class AuthorizationControllerServiceTest {

	private static final String FILE_PATH = PATH + CONTROLLERSERVICE + AUTHORIZATION;

	private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
	private MockRestServiceServer mockRestServiceServer;

	@Autowired
	private AuthorizationControllerService authorizationControllerService;

	@Autowired
	private RestOperations restOperations;

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Before
	public void setUp() {
		mockRestServiceServer = MockRestServiceServer.createServer((RestTemplate) restOperations);
	}

	@Test
	@DatabaseSetup(FILE_PATH + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void getShowWithoutPassword() throws Exception {
		AppointmentRequest appointmentRequest = authorizationControllerService.getAppointmentRequestIfAuthorized(false,
				2, null, null, null);
		assertEquals(appointmentRequest.getId().intValue(), 2);
	}

	@Test(expected = ResourceNotFoundException.class)
	@DatabaseSetup(FILE_PATH + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void getShowWithoutPasswordShouldFailDoesNotExist() throws Exception {
		authorizationControllerService.getAppointmentRequestIfAuthorized(false, 3, null, null, null);
	}

	@Test
	@DatabaseSetup(FILE_PATH + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void getShowWithPassword() throws Exception {
		AppointmentRequest appointmentRequest = authorizationControllerService.getAppointmentRequestIfAuthorized(false,
				1, null, ACCESS_TOKEN_PASSWORD, null);
		assertEquals(appointmentRequest.getId().intValue(), 1);
	}

	@Test(expected = ForbiddenException.class)
	@DatabaseSetup(FILE_PATH + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void getShowWithPasswordShouldFailWrongPassword() throws Exception {
		AppointmentRequest appointmentRequest = authorizationControllerService.getAppointmentRequestIfAuthorized(false,
				1, null, ACCESS_TOKEN_WRONG_PASSWORD, null);
		assertEquals(appointmentRequest.getId().intValue(), 1);
	}

	@Test
	@DatabaseSetup(FILE_PATH + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void getShowWithAdminPassword() throws Exception {
		AppointmentRequest appointmentRequest = authorizationControllerService.getAppointmentRequestIfAuthorized(false,
				1, null, ACCESS_TOKEN_ADMIN_PASSWORD, null);
		assertEquals(appointmentRequest.getId().intValue(), 1);
	}

	@Test
	@DatabaseSetup(FILE_PATH + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void getShowWithBackendUserAccessForShow() throws Exception {
		this.setMockRestServiceServer(AUTHUSER_1);

		AppointmentRequest appointmentRequest = authorizationControllerService.getAppointmentRequestIfAuthorized(false,
				1, ENTERED_JWTTOKEN, null, null);
		assertEquals(appointmentRequest.getId().intValue(), 1);
	}

	@Test(expected = ForbiddenException.class)
	@DatabaseSetup(FILE_PATH + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void getShowWithBackendUserAccessForShowShouldFailNoAccess() throws Exception {
		this.setMockRestServiceServer(AUTHUSER_3);

		authorizationControllerService.getAppointmentRequestIfAuthorized(false, 1, ENTERED_JWTTOKEN, null, null);
	}

	@Test
	@DatabaseSetup(FILE_PATH + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void getShowWithBackendUserAccessForEdit() throws Exception {
		this.setMockRestServiceServer(AUTHUSER_2);

		AppointmentRequest appointmentRequest = authorizationControllerService.getAppointmentRequestIfAuthorized(false,
				1, ENTERED_JWTTOKEN, null, null);
		assertEquals(appointmentRequest.getId().intValue(), 1);
	}

	@Test
	@DatabaseSetup(FILE_PATH + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + "/newShowAccess.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void getShowWithPasswordAddBackendUserAccessForShow() throws Exception {
		this.setMockRestServiceServer(AUTHUSER_3);

		AppointmentRequest appointmentRequest = authorizationControllerService.getAppointmentRequestIfAuthorized(false,
				1, ENTERED_JWTTOKEN, ACCESS_TOKEN_PASSWORD, null);
		assertEquals(appointmentRequest.getId().intValue(), 1);
	}

	@Test(expected = ForbiddenException.class)
	@DatabaseSetup(FILE_PATH + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void getShowWithPasswordAddBackendUserAccessForShowShouldFailWrongPassword() throws Exception {
		this.setMockRestServiceServer(AUTHUSER_3);

		authorizationControllerService.getAppointmentRequestIfAuthorized(false, 1, ENTERED_JWTTOKEN,
				ACCESS_TOKEN_WRONG_PASSWORD, null);
	}

	@Test
	@DatabaseSetup(FILE_PATH + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + "/newEditAccess.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void getShowWithAdminPasswordAddBackendUserAccessForEdit() throws Exception {
		this.setMockRestServiceServer(AUTHUSER_3);

		AppointmentRequest appointmentRequest = authorizationControllerService.getAppointmentRequestIfAuthorized(false,
				1, ENTERED_JWTTOKEN, ACCESS_TOKEN_ADMIN_PASSWORD, null);
		assertEquals(appointmentRequest.getId().intValue(), 1);
	}

	@Test
	@DatabaseSetup(FILE_PATH + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void getEditWithAdminPassword() throws Exception {
		AppointmentRequest appointmentRequest = authorizationControllerService.getAppointmentRequestIfAuthorized(true,
				1, null, ACCESS_TOKEN_ADMIN_PASSWORD, null);
		assertEquals(appointmentRequest.getId().intValue(), 1);
	}

	@Test(expected = ForbiddenException.class)
	@DatabaseSetup(FILE_PATH + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void getEditWithAdminPasswordShouldFailWrongPassword() throws Exception {
		authorizationControllerService.getAppointmentRequestIfAuthorized(true, 1, null, ACCESS_TOKEN_WRONG_PASSWORD,
				null);
	}

	@Test
	@DatabaseSetup(FILE_PATH + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void getEditWithBackendUserAccessForEdit() throws Exception {
		this.setMockRestServiceServer(AUTHUSER_2);

		AppointmentRequest appointmentRequest = authorizationControllerService.getAppointmentRequestIfAuthorized(true,
				1, ENTERED_JWTTOKEN, null, null);
		assertEquals(appointmentRequest.getId().intValue(), 1);
	}

	@Test(expected = ForbiddenException.class)
	@DatabaseSetup(FILE_PATH + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void getEditWithBackendUserAccessForEditShouldFailNoEditRights() throws Exception {
		this.setMockRestServiceServer(AUTHUSER_1);

		authorizationControllerService.getAppointmentRequestIfAuthorized(true, 1, ENTERED_JWTTOKEN, null, null);
	}

	@Test
	@DatabaseSetup(FILE_PATH + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + "/newEditAccess.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void getEditWithAdminPasswordAddBackendUserAccessForEdit() throws Exception {
		this.setMockRestServiceServer(AUTHUSER_3);

		AppointmentRequest appointmentRequest = authorizationControllerService.getAppointmentRequestIfAuthorized(true,
				1, ENTERED_JWTTOKEN, ACCESS_TOKEN_ADMIN_PASSWORD, null);
		assertEquals(appointmentRequest.getId().intValue(), 1);
	}

	@Test(expected = ForbiddenException.class)
	@DatabaseSetup(FILE_PATH + FILE_INITIAL)
	@ExpectedDatabase(value = FILE_PATH + FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void getEditWithAdminPasswordAddBackendUserAccessForEditShouldFailWrongPassword() throws Exception {
		this.setMockRestServiceServer(AUTHUSER_3);

		authorizationControllerService.getAppointmentRequestIfAuthorized(true, 1, ENTERED_JWTTOKEN,
				ACCESS_TOKEN_WRONG_PASSWORD, null);
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
