import { AppointmentRequest, AppointmentRequestConfig } from './appointment-request';
import { Appointment } from './appointment';
import { AppointmentMember } from './appointment-member';
import { AppointmentDecision, Decision } from './appointment-decision';

describe('Model: AppointmentRequest', () => {
  let appointmentRequest: AppointmentRequest;
  let appointment: Appointment;
  let appointmentMember: AppointmentMember;

  beforeEach(() => {
    appointment = new Appointment();
    appointment.id = 2;
    appointment.appointmentRequestId = 1;
    appointment.description = 'Test Event';

    appointmentMember = Object.assign(new AppointmentMember(), {
      id: 3,
      appointmentRequestId: 1,
      name: 'Test Name',
      appointmentDecisions: []
    });

    appointmentMember.appointmentDecisions.push(Object.assign(new AppointmentDecision(), {
      appointmentId: 2,
      appointmentMemberId: 3,
      decision: Decision.ACCEPT
    }));

    appointmentRequest = new AppointmentRequest(undefined);
    appointmentRequest.id = 1;
    appointmentRequest.title = 'test title';
    appointmentRequest.deadlineAsDate = new Date(2050, 10, 10);
    appointmentRequest.description = 'test description';
    appointmentRequest.appointmentRequestConfig = new AppointmentRequestConfig();
    appointmentRequest.appointmentRequestConfig.id = 1;
    appointmentRequest.organizerMail = 'name@mail.com';
    appointmentRequest.expired = false;

    appointmentRequest.appointments = [];
    appointmentRequest.appointments.push(appointment);

    appointmentRequest.appointmentMembers = [];
    appointmentRequest.addAppointmentMember(appointmentMember);
  });

  it('should have a deadline of "2050-11-10"', () => {
    expect(appointmentRequest.deadline).toEqual('2050-11-10');
  });

  it('should have a none empty decision statistic', () => {
    expect(appointmentRequest.decisionStatisticList.length).toBeGreaterThan(0);
  });

  it('should have a none empty favoriteAppointments list', () => {
    expect(appointmentRequest.favoriteAppointments.length).toBeGreaterThan(0);
  });

  it('toJSONTransferObject does not include private member variables', () => {
    let expectedObject = Object({
      organizerMail: 'name@mail.com',
      appointmentRequestConfig: Object({
        id: 1,
        appointmentConfig: {
          startDate: false,
          startTime: false,
          endTime: false,
          endDate: false,
          url: false,
          description: true
        },
        decisionType: 'DEFAULT'
      }),
      appointments: [
        Object({
          id: 2,
          description: 'Test Event',
          appointmentRequestId: 1
        })
      ],
      appointmentMembers: [
        Object({
          appointmentDecisions: [
            Object({
              appointmentId: 2,
              appointmentMemberId: 3,
              decision: 1
            })
          ],
          id: 3,
          appointmentRequestId: 1,
          name: 'Test Name'
        })
      ],
      id: 1,
      title: 'test title',
      deadline: '2050-11-10',
      expired: false,
      description: 'test description'
    });
    expect(JSON.parse(appointmentRequest.toJSONTransferObject())).toEqual(expectedObject);
  });
});

