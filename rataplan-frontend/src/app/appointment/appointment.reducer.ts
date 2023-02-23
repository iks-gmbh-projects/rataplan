import { AppointmentRequestModel } from "../models/appointment-request.model";
import { AppointmentAction, AppointmentActions } from "./appointment.actions";
import { isConfiguredEqual } from "../models/appointment.model";

export type appointmentRequestState = {
  appointmentRequest?: AppointmentRequestModel,
  complete: boolean,
  busy: boolean,
  error?: any,
};

function isComplete(appointmentRequest?: AppointmentRequestModel): boolean {
  return !!appointmentRequest &&
    !!appointmentRequest.title &&
    !!appointmentRequest.deadline &&
    appointmentRequest.appointments.length > 0;
}

export function appointmentRequestReducer(
  state: appointmentRequestState = {complete: false, busy: false},
  action: AppointmentAction
): appointmentRequestState {
  switch (action.type) {
    case AppointmentActions.INIT:
      return {
        complete: false,
        busy: true,
      }
    case AppointmentActions.INIT_SUCCESS:
      return {
        appointmentRequest: action.request,
        complete: isComplete(action.request),
        busy: false,
      };
    case AppointmentActions.INIT_ERROR:
      return {
        complete: false,
        busy: false,
        error: action.error,
      };
    case AppointmentActions.SET_GENERAL_VALUES:
      if(!state.appointmentRequest) return {
        complete: false,
        busy: false,
        error: {
          ...state.error,
          missing_request: "Initialize request first",
        },
      };
      return {
        appointmentRequest: {
          ...state.appointmentRequest,
          title: action.payload.title,
          description: action.payload.description,
          deadline: action.payload.deadline.toISOString(),
        },
        complete: false,
        busy: false,
      };
    case AppointmentActions.SET_APPOINTMENT_CONFIG:
      if(!state.appointmentRequest) return {
        complete: false,
        busy: false,
        error: {
          ...state.error,
          missing_request: "Initialize request first",
        },
      };
      if(isConfiguredEqual(state.appointmentRequest.appointmentRequestConfig.appointmentConfig, action.config)) return state;
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
        busy: false,
      };
    case AppointmentActions.SET_APPOINTMENTS: {
      if (!state.appointmentRequest) return {
        ...state,
        complete: false,
        error: {
          ...state.error,
          missing_request: "Initialize request first",
        },
      };
      const config = state.appointmentRequest.appointmentRequestConfig.appointmentConfig;
      if (!action.appointments.every(a =>
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
        busy: false,
      };
    }
    case AppointmentActions.ADD_APPOINTMENTS: {
      if (!state.appointmentRequest) return {
        ...state,
        complete: false,
        error: {
          ...state.error,
          missing_request: "Initialize request first",
        },
      };
      const config = state.appointmentRequest.appointmentRequestConfig.appointmentConfig;
      if (!action.appointments.every(a =>
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
        appointments: [...state.appointmentRequest.appointments, ...action.appointments],
      }
      return {
        appointmentRequest: request,
        complete: isComplete(request),
        busy: false,
      };
    }
    case AppointmentActions.REMOVE_APPOINTMENT: {
      if (!state.appointmentRequest) return {
        ...state,
        complete: false,
        error: {
          ...state.error,
          missing_request: "Initialize request first",
        },
      };
      const request: AppointmentRequestModel = {
        ...state.appointmentRequest,
        appointments: [...state.appointmentRequest.appointments.slice(0, action.index), ...state.appointmentRequest.appointments.slice(action.index+1)],
      }
      return {
        appointmentRequest: request,
        complete: isComplete(request),
        busy: false,
      };
    }
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
        busy: false,
      };
    case AppointmentActions.POST:
      return {
        ...state,
        busy: true,
      }
    case AppointmentActions.POST_SUCCESS:
      return {
        complete: false,
        busy: false,
      };
    case AppointmentActions.POST_ERROR:
      return {
        ...state,
        busy: false,
        error: action.error,
      }
  }
  return state;
}
