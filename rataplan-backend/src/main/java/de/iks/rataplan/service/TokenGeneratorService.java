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

    public String generateParticipationToken() {
        int leftLimit = 48; // number 0
        int rightLimit = 122; // letter z
        int stringLength = 8;

        while (true) {
            String participationToken = new Random()
                    .ints(leftLimit, rightLimit + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(stringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            System.out.println(participationToken);

            AppointmentRequest appointmentRequest = appointmentRequestRepository.findByParticipationToken(participationToken);
            System.out.println(appointmentRequest);
            if (appointmentRequest == null) {
                return participationToken;
            }
        }
    }
}
