package de.iks.rataplan.controller;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import de.iks.rataplan.config.AppConfig;
import de.iks.rataplan.config.TestConfig;
import de.iks.rataplan.domain.AppointmentRequest;
import de.iks.rataplan.repository.AppointmentRequestRepository;
import de.iks.rataplan.service.TokenGeneratorService;
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

import static de.iks.rataplan.testutils.TestConstants.*;
import static org.junit.Assert.*;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class, TestConfig.class })
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class GeneratorTokenTest {

    private static final String FILE_PATH = PATH + CONTROLLERSERVICE + GENERATORTOKEN;

    @Autowired
    private TokenGeneratorService tokenGeneratorService;

    @Autowired
    private AppointmentRequestRepository appointmentRequestRepository;

    @Test
    public void checkValidFormParticipationToken() throws Exception {
        String token = tokenGeneratorService.generateToken(8);
        assertTrue(String.format("Invalid Token: %s", token), token.matches("[a-zA-Z0-9]{8}"));
    }

    @Test
    public void checkValidFormEditToken() throws Exception {
        String token = tokenGeneratorService.generateToken(10);
        assertTrue(String.format("Invalid Token: %s", token), token.matches("[a-zA-Z0-9]{10}"));
    }

    @Test
    @DatabaseSetup(FILE_EMPTY_DB)
    public void generateParticipationToken() throws Exception {
        AppointmentRequest appointmentRequest = createSimpleAppointmentRequest();

        String token = tokenGeneratorService.generateToken(8);
        appointmentRequest.setParticipationToken(token);
        appointmentRequestRepository.saveAndFlush(appointmentRequest);

        AppointmentRequest createdAppointmentRequest = appointmentRequestRepository.findByParticipationToken(token);
        assertNotNull(createdAppointmentRequest.getParticipationToken());
    }

    @Test
    @DatabaseSetup(FILE_EMPTY_DB)
    public void generateEditToken() throws Exception {
        AppointmentRequest appointmentRequest = createSimpleAppointmentRequest();

        String token = tokenGeneratorService.generateToken(10);
        appointmentRequest.setEditToken(token);
        appointmentRequestRepository.saveAndFlush(appointmentRequest);

        AppointmentRequest createdAppointmentRequest = appointmentRequestRepository.findByEditToken(token);
        assertNotNull(createdAppointmentRequest.getEditToken());
    }

    @Test
    @DatabaseSetup(FILE_PATH + FILE_INITIAL)
    public void TestIfTokeIsUnique() throws Exception {
        assertTrue(tokenGeneratorService.isTokenUnique("coding02",8));
        assertFalse(tokenGeneratorService.isTokenUnique("coding01",8));
    }

    @Test
    @DatabaseSetup(FILE_PATH + FILE_INITIAL)
    public void generateNewTokenWhenExistsInDB() throws Exception {
        String token;
        if (!tokenGeneratorService.isTokenUnique("coding01", 8)) {
            token = tokenGeneratorService.generateToken(8);
            assertNotEquals("coding01", token);
        }
    }
}
