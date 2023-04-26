import { AppointmentRequestModel } from "../models/appointment-request.model";
import { ActionRequiresInit, AppointmentAction, AppointmentActions } from "./appointment.actions";
import { isConfiguredEqual, matchConfiguration, matchesConfiguration } from "../models/appointment.model";

export type appointmentRequestState = {
  busy: boolean,
  error?: any,
} & ({
  appointmentRequest?: undefined,
  complete?: false,
  appointmentsChanged?: undefined,
} | {
  appointmentRequest: AppointmentRequestModel,
  complete: boolean,
  appointmentsChanged: boolean,
});

function isComplete(appointmentRequest?: AppointmentRequestModel): boolean {
  return !!appointmentRequest &&
    !!appointmentRequest.title &&
    !!appointmentRequest.deadline &&
    appointmentRequest.appointments.length > 0;
}

function assembleRequestState(request: AppointmentRequestModel, appointmentsChanged: boolean): appointmentRequestState {
  return {
    appointmentRequest: request,
    complete: isComplete(request),
    appointmentsChanged: appointmentsChanged,
    busy: false,
  };
}

export function appointmentRequestReducer(
  state: appointmentRequestState = {complete: false, busy: false},
  action: AppointmentAction,
): appointmentRequestState {
  if (ActionRequiresInit[action.type] && !state.appointmentRequest) {
    return {
      complete: false,
      busy: false,
      error: {
        ...state.error,
        missing_request: "Initialize request first",
      },
    };
  }
  if ("appointments" in action && !matchConfiguration(action.appointments, state.appointmentRequest!.appointmentRequestConfig.appointmentConfig)) {
    return {
      ...state,
      error: {
        ...state.error,
        invalid_appointment: "Appointment information does not match configuration",
      },
    };
  }
  switch (action.type) {
    case AppointmentActions.INIT:
      return {
        complete: false,
        busy: true,
      };
    case AppointmentActions.INIT_SUCCESS:
      return {
        appointmentRequest: action.request,
        complete: isComplete(action.request),
        appointmentsChanged: false,
        busy: false,
      };
    case AppointmentActions.INIT_ERROR:
      return {
        complete: false,
        busy: false,
        error: action.error,
      };
    case AppointmentActions.SET_GENERAL_VALUES:
      return assembleRequestState(
        {
          ...state.appointmentRequest!,
          title: action.payload.title,
          description: action.payload.description,
          deadline: action.payload.deadline.toISOString(),
          appointmentRequestConfig: {
            ...state.appointmentRequest!.appointmentRequestConfig,
            decisionType: action.payload.decisionType,
          },
        },
        state.appointmentsChanged!,
      );
    case AppointmentActions.SET_APPOINTMENT_CONFIG:
      if (isConfiguredEqual(
        state.appointmentRequest!.appointmentRequestConfig.appointmentConfig,
        action.config,
      )) {
        return state;
      }
      return {
        appointmentRequest: {
          ...state.appointmentRequest!,
          appointmentRequestConfig: {
            ...state.appointmentRequest!.appointmentRequestConfig,
            appointmentConfig: {...action.config},
          },
          appointments: [],
        },
        complete: false,
        appointmentsChanged: true,
        busy: false,
      };
    case AppointmentActions.SET_APPOINTMENTS:
      return assembleRequestState(
        {
          ...state.appointmentRequest!,
          appointments: [...action.appointments],
        },
        true,
      );
    case AppointmentActions.ADD_APPOINTMENTS:
      return assembleRequestState({
          ...state.appointmentRequest!,
          appointments: [...state.appointmentRequest!.appointments, ...action.appointments],
        },
        true,
      );
    case AppointmentActions.EDIT_APPOINTMENT:
      if (!matchesConfiguration(
        action.appointment,
        state.appointmentRequest!.appointmentRequestConfig.appointmentConfig,
      )) return {
        ...state,
        error: {
          ...state.error,
          invalid_appointment: "Appointment information does not match configuration",
        },
      };
      return assembleRequestState(
        {
          ...state.appointmentRequest!,
          appointments: [
            ...state.appointmentRequest!.appointments.slice(0, action.index),
            action.appointment,
            ...state.appointmentRequest!.appointments.slice(action.index + 1),
          ],
        },
        true,
      );
    case AppointmentActions.REMOVE_APPOINTMENT:
      return assembleRequestState(
        {
          ...state.appointmentRequest!,
          appointments: [
            ...state.appointmentRequest!.appointments.slice(0, action.index),
            ...state.appointmentRequest!.appointments.slice(action.index + 1),
          ],
        },
        true,
      );
    case AppointmentActions.SET_ORGANIZER_INFO:
      return {
        appointmentRequest: {
          ...state.appointmentRequest!,
          organizerName: action.payload.name,
          organizerMail: action.payload.email,
          consigneeList: action.payload.consigneeList,
        },
        complete: state.complete!,
        appointmentsChanged: state.appointmentsChanged!,
        busy: false,
      };
    case AppointmentActions.POST:
      return {
        ...state,
        busy: true,
      };
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
      };
  }
  return state;
}
