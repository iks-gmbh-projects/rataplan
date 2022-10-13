package de.iks.rataplan.controller;

import static de.iks.rataplan.testutils.ITConstants.FILE_EXPECTED;
import static de.iks.rataplan.testutils.ITConstants.FILE_INITIAL;
import static de.iks.rataplan.testutils.ITConstants.USERS;
import static de.iks.rataplan.testutils.ITConstants.USER_1;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.iks.rataplan.config.AppConfig;
import de.iks.rataplan.config.IntegrationConfig;
import de.iks.rataplan.domain.User;

@ActiveProfiles("integration")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class, IntegrationConfig.class })
@WebAppConfiguration
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class RataplanAuthRestControllerIT {

	private static final String BASE_LINK = "classpath:test/db/service/user";
	private static final String USER_FILE_INITIAL = BASE_LINK + FILE_INITIAL;
	private static final String USER_FILE_EXPECTED = BASE_LINK + FILE_EXPECTED;

	public static final String REGISTER = USERS + "/register";
	public static final String LOGIN = USERS + "/login";
	public static final String PROFILE = USERS + "/profile";

	private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
	private MockMvc mockMvc;

	@Resource
	private WebApplicationContext webApplicationContext;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	@DatabaseSetup(USER_FILE_INITIAL)
	@ExpectedDatabase(value = USER_FILE_EXPECTED, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void registerUser() throws Exception {

		String json = gson.toJson(USER_1);
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(REGISTER);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());
		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isCreated());
	}
	
	@Test
	@DatabaseSetup(USER_FILE_INITIAL)
	@ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void registerUserShouldFailUsernameAlreadyExists() throws Exception {

		String json = gson.toJson(new User(1, "neuerpeter@sch.mitz", "PeTEr", "password", "peter"));
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(REGISTER);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());
		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isGone());
	}
	
	@Test
	@DatabaseSetup(USER_FILE_INITIAL)
	@ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void registerUserShouldFailMailAlreadyExists() throws Exception {

		String json = gson.toJson(new User(1, "pETer@sch.mitz", "PeTEer", "password", "peter"));
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(REGISTER);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());
		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isGone());
	}
	
	@Test
	@DatabaseSetup(USER_FILE_INITIAL)
	@ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void loginUser() throws Exception {

		String json = gson.toJson(new User(1, "pEtEr@sCh.mITz", null, "geheim", null));
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(LOGIN);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());
		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	@DatabaseSetup(USER_FILE_INITIAL)
	@ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void loginUserShouldFailWrongPassword() throws Exception {

		String json = gson.toJson(new User(1, "peter@sch.mitz", null, "geheIm", null));
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(LOGIN);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());
		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@DatabaseSetup(USER_FILE_INITIAL)
	@ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void loginUserShouldFailMailDoesNotExist() throws Exception {

		String json = gson.toJson(new User(1, "peteerr@sch.mitz", null, "geheim", null));
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(LOGIN);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());
		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@DatabaseSetup(USER_FILE_INITIAL)
	@ExpectedDatabase(value = USER_FILE_INITIAL, assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void loginUserShouldFailUsernameDoesNotExist() throws Exception {

		String json = gson.toJson(new User(1, null, "doesnotexist", "geheim", null));
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(LOGIN);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());
		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	
}
