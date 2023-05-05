package de.iks.rataplan.service;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.iks.rataplan.domain.AppointmentDecision;
import de.iks.rataplan.domain.AppointmentMember;
import de.iks.rataplan.domain.AppointmentRequest;
import de.iks.rataplan.exceptions.ForbiddenException;
import de.iks.rataplan.exceptions.MalformedException;
import de.iks.rataplan.repository.AppointmentMemberRepository;
import de.iks.rataplan.repository.AppointmentRequestRepository;

@Service
@Transactional
public class AppointmentMemberServiceImpl implements AppointmentMemberService {

    @Autowired
    private AppointmentRequestRepository appointmentRequestRepository;

    @Autowired
    private AppointmentMemberRepository appointmentMemberRepository;

    @Override
    public AppointmentMember createAppointmentMember(AppointmentRequest appointmentRequest, AppointmentMember appointmentMember) {
        
        this.validateExpirationDate(appointmentRequest);
		
        appointmentMember.setId(null);

        if (appointmentRequest.validateDecisionsForAppointmentMember(appointmentMember)) {
        	
            appointmentMember.setAppointmentRequest(appointmentRequest);
            appointmentRequest.getAppointmentMembers().add(appointmentMember);

            for (AppointmentDecision decision : appointmentMember.getAppointmentDecisions()) {
                decision.setAppointmentMember(appointmentMember);
            }

            return appointmentMemberRepository.saveAndFlush(appointmentMember);
        } else {
            throw new MalformedException(
                    "AppointmentDecisions don't fit the DecisionType in the AppointmentRequest.");
        }
    }

	@Override
    public void deleteAppointmentMember(AppointmentRequest appointmentRequest, AppointmentMember appointmentMember) {

        this.validateExpirationDate(appointmentRequest);
        
        appointmentRequest.getAppointmentMembers().remove(appointmentMember);
        appointmentRequestRepository.saveAndFlush(appointmentRequest);
    }

    @Override
    public AppointmentMember updateAppointmentMember(AppointmentRequest appointmentRequest, AppointmentMember dbAppointmentMember,
            AppointmentMember newAppointmentMember) {
        
        this.validateExpirationDate(appointmentRequest);

        if (!appointmentRequest.validateDecisionsForAppointmentMember(newAppointmentMember)) {
        	throw new MalformedException(
        			"AppointmentDecisions don't fit the DecisionType in the AppointmentRequest.");
        }

        dbAppointmentMember.setName(newAppointmentMember.getName());
        dbAppointmentMember.setAppointmentRequest(appointmentRequest);
        this.updateAppointmentDecisionsForMember(dbAppointmentMember.getAppointmentDecisions(), newAppointmentMember.getAppointmentDecisions());
        return appointmentMemberRepository.saveAndFlush(dbAppointmentMember);
    }
    
    @Override
    public void anonymizeAppointmentMember(int id) {
        AppointmentMember member = appointmentMemberRepository.findOne(id);
        member.setName(null);
        member.setUserId(null);
        appointmentMemberRepository.saveAndFlush(member);
    }
    
    /**
     * updates the oldDecisions decisions to the new ones based on the
     * appointmentId's
     *
     * @param oldDecisions
     * @param newDecisions
     */
    private void updateAppointmentDecisionsForMember(List<AppointmentDecision> oldDecisions,
            List<AppointmentDecision> newDecisions) {
        for (AppointmentDecision appointmentDecision : oldDecisions) {
            for (AppointmentDecision newdecision : newDecisions) {
                if (Objects.equals(appointmentDecision.getAppointment().getId(), newdecision.getAppointment().getId())) {
                    appointmentDecision.setDecision(newdecision.getDecision());
                    appointmentDecision.setParticipants(newdecision.getParticipants());
                }
            }
        }
    }
    
    private void validateExpirationDate(AppointmentRequest appointmentRequest) {
    	if (appointmentRequest.isNotified()) {
			throw new ForbiddenException("Appointmentrequest ist expired!");
		}
    }
}
