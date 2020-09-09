package de.iks.rataplan.utils;

import java.util.ArrayList;
import java.util.List;

import de.iks.rataplan.domain.Appointment;
import de.iks.rataplan.domain.AppointmentMember;

public class AppointmentRequestBuilder {

	private AppointmentRequestBuilder() {
		// nothing to do here
	}

	public static List<Appointment> appointmentList(Appointment... appointments) {
		List<Appointment> appointmentList = new ArrayList<>();
		for (Appointment appointment : appointments) {
			appointmentList.add(appointment);
		}
		return appointmentList;
	}

	public static List<AppointmentMember> memberList(AppointmentMember... appointmentMembers) {
		List<AppointmentMember> appointmentMemberList = new ArrayList<>();
		for (AppointmentMember appointmentMember : appointmentMembers) {
			appointmentMemberList.add(appointmentMember);
		}
		return appointmentMemberList;
	}
}
