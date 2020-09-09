package de.iks.rataplan.controller;

import static de.iks.rataplan.testutils.ITConstants.CONTACTS;
import static de.iks.rataplan.testutils.ITConstants.VERSION;
import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Ignore;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.iks.rataplan.config.AppConfig;
import de.iks.rataplan.config.IntegrationConfig;
import de.iks.rataplan.domain.ContactData;

@ActiveProfiles("integration")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@Transactional
@ContextConfiguration(classes = { AppConfig.class, IntegrationConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
	TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class GeneralIT {

	private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
	private MockMvc mockMvc;
	
	@Resource
	private WebApplicationContext webApplicationContext;
	
	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	public void sendContactData() throws Exception {

		String json = gson.toJson(new ContactData("subject", "content", "iks@iks-gmbh.com"));
		
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(VERSION + CONTACTS);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());

		MvcResult mvcResult = this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		
		assertEquals(mvcResult.getResponse().getContentAsString(), "true");
	}
	
	@Test
	public void sendContactDataShouldFailNoContent() throws Exception {

		String json = gson.toJson(new ContactData("subject", null, "iks@iks-gmbh.com"));
		
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(VERSION + CONTACTS);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isBadRequest())
		.andReturn();
	}

	@Test
	public void sendContactDataShouldFailNoSubject() throws Exception {

		String json = gson.toJson(new ContactData(null, "content", "iks@iks-gmbh.com"));
		
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(VERSION + CONTACTS);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isBadRequest())
		.andReturn();
	}

	@Test
	public void sendContactDataShouldFailNoSenderMail() throws Exception {

		String json = gson.toJson(new ContactData("subject", "content", null));
		
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(VERSION + CONTACTS);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isBadRequest())
		.andReturn();
	}

	@Ignore
	@Test
	public void sendContactDataShouldFailInvalidSenderMail() throws Exception {

		String json = gson.toJson(new ContactData("subject", "content", "ich-bin-keine-mail"));
		
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(VERSION + CONTACTS);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8);
		requestBuilder.content(json.getBytes());

		this.mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isIAmATeapot())
		.andReturn();
	}
	
}
