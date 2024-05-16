package de.iks.rataplan.service;

import de.iks.rataplan.domain.notifications.EmailCycle;
import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Transactional
@Component
@RequiredArgsConstructor
public class SchedulerService {
    private final NotificationService notificationService;
    
    @Scheduled(cron = "0 2 0 1-5 * *")
    public void sendDailyDigests() {
        notificationService.sendSummary(Set.of(EmailCycle.DAILY_DIGEST));
    }
    
    @Scheduled(cron = "0 2 0 6 * *")
    public void sendWeeklyDigests() {
        notificationService.sendSummary(Set.of(EmailCycle.DAILY_DIGEST, EmailCycle.WEEKLY_DIGEST));
    }
}