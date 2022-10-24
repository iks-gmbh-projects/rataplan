package de.iks.rataplan.service;

import de.iks.rataplan.domain.AppointmentRequest;
import de.iks.rataplan.repository.AppointmentRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class TokenGeneratorService {

    @Autowired
    private AppointmentRequestRepository appointmentRequestRepository;

    public String generateParticipationToken(int length) {
        int leftLimit = 48; // number 0
        int rightLimit = 122; // letter z

        while (true) {
            String token = new Random()
                    .ints(leftLimit, rightLimit + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            AppointmentRequest appointmentRequest;
            if (length == 8) {
                appointmentRequest = appointmentRequestRepository.findByParticipationToken(token);
            } else {
                appointmentRequest = appointmentRequestRepository.findByEditToken(token);
            }
            if (appointmentRequest == null) {
                return token;
            }
        }
    }
}
