package de.iks.rataplan.service;

import java.util.List;

import de.iks.rataplan.domain.AppointmentRequest;

public interface AppointmentRequestService {
    public List<AppointmentRequest> getAppointmentRequests();
    public List<AppointmentRequest> getAppointmentRequestsForUser(Integer userId);
    public AppointmentRequest getAppointmentRequestById(Integer requestId);
    public AppointmentRequest getAppointmentRequestByParticipationToken(String participationToken);
    public AppointmentRequest getAppointmentRequestByEditToken(String editToken);
    public AppointmentRequest createAppointmentRequest(AppointmentRequest appointmentRequest);
    public AppointmentRequest updateAppointmentRequest(AppointmentRequest dbAppointmentRequest, AppointmentRequest newAppointmentRequest);
    public List<AppointmentRequest> getAppointmentRequestsWhereUserTakesPartIn(Integer userId);
    public void deleteAppointmentRequest(AppointmentRequest request);
    public void anonymizeAppointmentRequests(Integer userId);
}

