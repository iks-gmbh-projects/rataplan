package de.iks.rataplan.controller;

import de.iks.rataplan.domain.AppointmentMember;
import de.iks.rataplan.domain.AppointmentRequest;
import de.iks.rataplan.service.AppointmentMemberService;
import de.iks.rataplan.service.AppointmentRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * Endpoints to recieve notifications from the Auth-Backend
 */
@RestController
@RequestMapping("/backend")
public class BackendController {
    private final AppointmentMemberService appointmentMemberService;
    private final AppointmentRequestService appointmentRequestService;
    
    public BackendController(
        AppointmentMemberService appointmentMemberService,
        AppointmentRequestService appointmentRequestService
    ) {
        this.appointmentMemberService = appointmentMemberService;
        this.appointmentRequestService = appointmentRequestService;
    }
    
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteData(@PathVariable int userId, @RequestBody(required = false) String secret) {
        //TODO validate secret
        appointmentRequestService.getAppointmentRequestsWhereUserTakesPartIn(userId)
            .stream()
            .map(AppointmentRequest::getAppointmentMembers)
            .flatMap(List::stream)
            .filter(m -> Objects.equals(m.getUserId(), userId))
            .mapToInt(AppointmentMember::getId)
            .forEach(appointmentMemberService::anonymizeAppointmentMember);
        appointmentRequestService.getAppointmentRequestsForUser(userId)
            .forEach(appointmentRequestService::deleteAppointmentRequest);
        return ResponseEntity.ok(userId);
    }
    
    @PostMapping("/{userId}/anonymize")
    public ResponseEntity<?> anonymizeData(@PathVariable int userId, @RequestBody(required = false) String secret) {
        //TODO validate secret
        appointmentRequestService.getAppointmentRequestsWhereUserTakesPartIn(userId)
            .stream()
            .map(AppointmentRequest::getAppointmentMembers)
            .flatMap(List::stream)
            .filter(m -> Objects.equals(m.getUserId(), userId))
            .mapToInt(AppointmentMember::getId)
            .forEach(appointmentMemberService::anonymizeAppointmentMember);
        appointmentRequestService.anonymizeAppointmentRequests(userId);
        return ResponseEntity.ok(userId);
    }
}
