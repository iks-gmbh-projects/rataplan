package de.iks.rataplan.service;

import de.iks.rataplan.domain.Vote;
import de.iks.rataplan.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final VoteRepository voteRepository;
    
    private final NotificationService notificationService;
    
    
    @Scheduled(cron = "0 1 0 ? * *")    // Jeden Tag um 0:01 Uhr
//	@Scheduled(fixedRate = 10000)		// Alle 10 Sekunden
    public void reportCurrentTime() {

        List<Vote> requests = voteRepository.findByDeadlineBeforeAndNotifiedFalse(Instant.now());

        for (Vote request : requests) {
            try {
                notificationService.notifyForVoteExpired(request);
                request.setNotified(true);
                voteRepository.saveAndFlush(request);
            } catch(RuntimeException ex) {
                log.error("Unexpected exception while sending expiration notifications", ex);
            }
        }
    }
}