package de.iks.rataplan.service;

import de.iks.rataplan.config.JwtConfig;
import de.iks.rataplan.domain.notifications.EmailCycle;
import de.iks.rataplan.repository.AuthTokenRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Set;

@Transactional
@Component
@RequiredArgsConstructor
public class SchedulerService {
    private final JwtConfig tokenConfig;
    private final NotificationService notificationService;
    private final AuthTokenRepository authTokenRepository;
    
    @Scheduled(cron = "0 1 0 ? * *") // Jeden Tag um 0:01 Uhr
    public void deleteOldAuthToken() {
        
        Date currentDate = new java.util.Date();
        currentDate.setTime(currentDate.getTime() - tokenConfig.getLifetime()*1000);
        
        authTokenRepository.deleteAllByCreatedDateTimeIsBefore(currentDate);
    }
    
    @Scheduled(cron = "0 2 0 1-5 * *")
    public void sendDailyDigests() {
        notificationService.sendSummary(Set.of(EmailCycle.DAILY_DIGEST));
    }
    
    @Scheduled(cron = "0 2 0 6 * *")
    public void sendWeeklyDigests() {
        notificationService.sendSummary(Set.of(EmailCycle.DAILY_DIGEST, EmailCycle.WEEKLY_DIGEST));
    }
}