package de.iks.rataplan.controller;

import de.iks.rataplan.domain.AppointmentMember;
import de.iks.rataplan.domain.AppointmentRequest;
import de.iks.rataplan.domain.BackendUser;
import de.iks.rataplan.service.AppointmentMemberService;
import de.iks.rataplan.service.AppointmentRequestService;
import de.iks.rataplan.service.BackendUserService;
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
    private final BackendUserService backendUserService;
    
    public BackendController(
        AppointmentMemberService appointmentMemberService,
        AppointmentRequestService appointmentRequestService,
        BackendUserService backendUserService
    ) {
        this.appointmentMemberService = appointmentMemberService;
        this.appointmentRequestService = appointmentRequestService;
        this.backendUserService = backendUserService;
    }
    
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteData(@PathVariable int userId, @RequestBody String secret) {
        //TODO validate secret
        final BackendUser user = backendUserService.getBackendUserByAuthUserId(userId);
        if(user != null) {
            appointmentRequestService.getAppointmentRequestsWhereUserTakesPartIn(user.getId())
                .stream()
                .map(AppointmentRequest::getAppointmentMembers)
                .flatMap(List::stream)
                .filter(m -> Objects.equals(m.getBackendUserId(), user.getId()))
                .mapToInt(AppointmentMember::getId)
                .forEach(appointmentMemberService::anonymizeAppointmentMember);
            appointmentRequestService.getAppointmentRequestsForUser(user.getId())
                .forEach(appointmentRequestService::deleteAppointmentRequest);
            backendUserService.deleteBackendUser(user);
        }
        return ResponseEntity.ok(userId);
    }
    
    @PostMapping("/{userId}/anonymize")
    public ResponseEntity<?> anonymizeData(@PathVariable int userId, @RequestBody String secret) {
        //TODO validate secret
        final BackendUser user = backendUserService.getBackendUserByAuthUserId(userId);
        if(user != null) {
            appointmentRequestService.getAppointmentRequestsWhereUserTakesPartIn(user.getId())
                .stream()
                .map(AppointmentRequest::getAppointmentMembers)
                .flatMap(List::stream)
                .filter(m -> Objects.equals(m.getBackendUserId(), user.getId()))
                .mapToInt(AppointmentMember::getId)
                .forEach(appointmentMemberService::anonymizeAppointmentMember);
            appointmentRequestService.anonymizeAppointmentRequests(user.getId());
            backendUserService.deleteBackendUser(user);
        }
        return ResponseEntity.ok(userId);
    }
}
