package de.iks.rataplan.service;

import java.util.List;

import de.iks.rataplan.domain.Vote;

public interface AppointmentRequestService {
    public List<Vote> getAppointmentRequests();
    public List<Vote> getAppointmentRequestsForUser(Integer userId);
    public Vote getAppointmentRequestById(Integer requestId);
    public Vote getAppointmentRequestByParticipationToken(String participationToken);
    public Vote getAppointmentRequestByEditToken(String editToken);
    public Vote createAppointmentRequest(Vote vote);
    public Vote updateAppointmentRequest(Vote dbVote, Vote newVote);
    public List<Vote> getAppointmentRequestsWhereUserTakesPartIn(Integer userId);
    public void deleteAppointmentRequest(Vote request);
    public void anonymizeAppointmentRequests(Integer userId);
}

