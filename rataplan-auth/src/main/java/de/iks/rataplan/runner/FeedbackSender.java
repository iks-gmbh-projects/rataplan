package de.iks.rataplan.runner;

import de.iks.rataplan.service.FeedbackService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeedbackSender {
    private final FeedbackService feedbackService;
    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.DAYS)
    public void sendFeedback() {
        log.info("Sending feedback report");
        feedbackService.sendFeedback();
        log.info("Sent feedback report");
    }
}
