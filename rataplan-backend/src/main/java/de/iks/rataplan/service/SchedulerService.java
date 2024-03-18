package de.iks.rataplan.service;

import de.iks.rataplan.domain.Vote;
import de.iks.rataplan.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SchedulerService {

    private final VoteRepository voteRepository;

    private final MailService mailService;


    @Scheduled(cron = "0 1 0 ? * *")    // Jeden Tag um 0:01 Uhr
//	@Scheduled(fixedRate = 10000)		// Alle 10 Sekunden
    public void reportCurrentTime() {

        List<Vote> requests = voteRepository.findByDeadlineBeforeAndNotifiedFalse(new Date(Calendar.getInstance().getTimeInMillis()));

        for (Vote request : requests) {

            request.setNotified(true);
            voteRepository.saveAndFlush(request);

            if (request.getNotificationSettings() != null) {
                mailService.sendMailForVoteExpired(request);
            }
        }
    }
}
