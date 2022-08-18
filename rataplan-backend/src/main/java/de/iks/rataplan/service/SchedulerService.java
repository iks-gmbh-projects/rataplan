package de.iks.rataplan.service;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.iks.rataplan.domain.AppointmentRequest;
import de.iks.rataplan.repository.AppointmentRequestRepository;

@Component
public class SchedulerService {

    @Autowired
    private AppointmentRequestRepository appointmentRequestRepository;

    @Autowired
    private MailService mailService;


    @Scheduled(cron = "0 1 0 ? * *")    // Jeden Tag um 0:01 Uhr
//	@Scheduled(fixedRate = 10000)		// Alle 10 Sekunden
    public void reportCurrentTime() {

        List<AppointmentRequest> requests = appointmentRequestRepository.findByDeadlineBeforeAndExpiredFalse(new Date(Calendar.getInstance().getTimeInMillis()));

        for (AppointmentRequest request : requests) {

            request.setExpired(true);
            appointmentRequestRepository.saveAndFlush(request);

            if (request.getOrganizerMail() != null) {
                mailService.sendMailForAppointmentRequestExpired(request);
            }
        }
    }

    @Scheduled(fixedRate = 20000)
    public void deleteOldAuthToken() {
        //authService
    }
}
