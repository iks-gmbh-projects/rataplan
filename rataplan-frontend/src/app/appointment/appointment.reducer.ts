import { AppointmentRequestModel } from "../models/appointment-request.model";
import { AppointmentAction, AppointmentActions } from "./appointment.actions";

export type appointmentRequestState = {
  appointmentRequest?: AppointmentRequestModel,
  complete: boolean,
  error?: any,
};

const emptyAppointmentRequest: AppointmentRequestModel = {
  title: "",
  deadline: "",
  appointmentRequestConfig: {
    appointmentConfig: {
      startDate: true,
      startTime: false,
      endDate: false,
      endTime: false,
      description: false,
      url: false,
    },
    decisionType: "DEFAULT",
  },
  appointments: [],
  appointmentMembers: [],
  consigneeList: [],
};

function isComplete(appointmentRequest?: AppointmentRequestModel): boolean {
  return !!appointmentRequest &&
    !!appointmentRequest.title &&
    !!appointmentRequest.deadline &&
    appointmentRequest.appointments.length > 0;
}

export function appointmentRequestReducer(
  state: appointmentRequestState = {complete: false},
  action: AppointmentAction
): appointmentRequestState {
  switch (action.type) {
    case AppointmentActions.SET_GENERAL_VALUES:
      return {
        appointmentRequest: {
          ...emptyAppointmentRequest,
          title: action.payload.title,
          description: action.payload.description,
          deadline: action.payload.deadline.toISOString(),
        },
        complete: false,
      };
    case AppointmentActions.SET_APPOINTMENT_CONFIG:
      if(!state.appointmentRequest) return {
        complete: false,
        error: {
          ...state.error,
          missing_request: "Initialize request first",
        },
      };
      return {
        appointmentRequest: {
          ...state.appointmentRequest,
          appointmentRequestConfig: {
            ...state.appointmentRequest.appointmentRequestConfig,
            appointmentConfig: {...action.config},
          },
          appointments: [],
        },
        complete: false,
      };
    case AppointmentActions.SET_APPOINTMENTS:
      if(!state.appointmentRequest) return {
        ...state,
        complete: false,
        error: {
          ...state.error,
          missing_request: "Initialize request first",
        },
      };
      const config = state.appointmentRequest.appointmentRequestConfig.appointmentConfig;
      if(!action.appointments.every(a =>
        ((config.startDate || config.startTime) == !!a.startDate) &&
        ((config.endDate || config.endTime) == !!a.endDate) &&
        (config.description == !!a.description) &&
        (config.url == !!a.url)
      )) return {
        ...state,
        complete: false,
        error: {
          ...state.error,
          invalid_appointment: "Appointment information does not match configuration",
        }
      };
      const request: AppointmentRequestModel = {
        ...state.appointmentRequest,
        appointments: [...action.appointments],
      }
      return {
        appointmentRequest: request,
        complete: isComplete(request),
      };
    case AppointmentActions.SET_ORGANIZER_INFO:
      if(!state.appointmentRequest) return {
        ...state,
        complete: false,
        error: {
          ...state.error,
          missing_request: "Initialize request first",
        },
      };
      return {
        appointmentRequest: {
          ...state.appointmentRequest,
          organizerName: action.payload.name,
          organizerMail: action.payload.email,
          consigneeList: action.payload.consigneeList,
        },
        complete: state.complete,
      };
    case AppointmentActions.POST_SUCCESS:
      return {
        complete: false,
      };
    case AppointmentActions.POST_ERROR:
      return {
        ...state,
        error: action.error,
      }
  }
  return state;
}
