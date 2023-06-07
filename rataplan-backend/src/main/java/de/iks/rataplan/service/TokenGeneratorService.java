package de.iks.rataplan.service;

import de.iks.rataplan.domain.Vote;
import de.iks.rataplan.repository.AppointmentRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class TokenGeneratorService {

    @Autowired
    private AppointmentRequestRepository appointmentRequestRepository;

    private final Random random = new Random();

    public String generateToken(int length) {
        int leftLimit = 48; // number 0
        int rightLimit = 122; // letter z

        while (true) {
            String token = random
                    .ints(leftLimit, rightLimit + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            if (isTokenUnique(token, length)) {
                return token;
            }
        }
    }

    public boolean isTokenUnique(String token, int length) {
        Vote vote;
        if (length == 8) {
            vote = appointmentRequestRepository.findByParticipationToken(token);
        } else if (length == 10) {
            vote = appointmentRequestRepository.findByEditToken(token);
        } else {
            throw new Error("appointmentRequest token does not fit the form");
        }
        return vote == null;
    }
}
