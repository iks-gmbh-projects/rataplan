package de.iks.rataplan.service;

import de.iks.rataplan.repository.AuthTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


@Transactional
@Component
@RequiredArgsConstructor
public class SchedulerService {
    private final AuthTokenRepository authTokenRepository;

    @Value("${token.lifetime}")
    private int tokenLifetime;


    @Scheduled(cron = "0 1 0 ? * *") // Jeden Tag um 0:01 Uhr
    public void deleteOldAuthToken() {

        Date currentDate = new java.util.Date();
        currentDate.setTime(currentDate.getTime() - tokenLifetime);

        authTokenRepository.deleteAllByCreatedDateTimeIsBefore(currentDate);
    }
}
