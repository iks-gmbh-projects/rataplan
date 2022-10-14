package de.iks.rataplan.restservice;

import static de.iks.rataplan.testutils.TestConstants.AUTHUSER_1;
import static de.iks.rataplan.testutils.TestConstants.RETURNED_JWTTOKEN;
import static de.iks.rataplan.testutils.TestConstants.HEADER_JWTTOKEN;
import static de.iks.rataplan.testutils.TestConstants.ENTERED_JWTTOKEN;
import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import de.iks.rataplan.config.AppConfig;
import de.iks.rataplan.config.TestConfig;
import de.iks.rataplan.domain.AuthUser;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class, TestConfig.class })
public class AuthServiceTest {

	// muss an der Stelle AuthServiceImpl sein, da ansonsten keine Instanz erstellt werden kann
	@InjectMocks
	private AuthServiceImpl authService;
	
	@Mock
	private RestTemplate restTemplate;

    @Before
    public void initMocks(){
        MockitoAnnotations.initMocks(this);
    }
	
	@Test
	public void getUserData() {
		HttpHeaders responseHeaders = createResponseHeaders();
		AuthUser authUser = AUTHUSER_1;
		ResponseEntity<AuthUser> response = new ResponseEntity<AuthUser>(authUser, responseHeaders, HttpStatus.OK);
		
		when(restTemplate.exchange(
				Matchers.anyString(), 
				Matchers.any(HttpMethod.class), 
				Matchers.<HttpEntity<?>>any(),
				Matchers.<Class<AuthUser>>any()))
		.thenReturn(response);
		
		ResponseEntity<AuthUser> responseEntity = authService.getUserData(ENTERED_JWTTOKEN);
		assertEquals(RETURNED_JWTTOKEN, responseEntity.getHeaders().getFirst(HEADER_JWTTOKEN));
		assertEquals(authUser, responseEntity.getBody());
	}
	
	@Test(expected = HttpClientErrorException.class)
	public void getUserDataShouldFailInvalidToken() {
		
		when(restTemplate.exchange(
				Matchers.anyString(), 
				Matchers.any(HttpMethod.class), 
				Matchers.<HttpEntity<?>>any(),
				Matchers.<Class<AuthUser>>any()))
		.thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN, "Invalid token you have"));
		
		authService.getUserData(ENTERED_JWTTOKEN);
	}
	
	private HttpHeaders createResponseHeaders() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(HEADER_JWTTOKEN, RETURNED_JWTTOKEN);
		return responseHeaders;
	}
}
