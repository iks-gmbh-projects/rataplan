import { Action } from "@ngrx/store";
import { AppointmentConfig, AppointmentModel } from "../models/appointment.model";
import { AppointmentRequestModel } from "../models/appointment-request.model";

export const AppointmentActions: {
  readonly SET_GENERAL_VALUES: "[appointmentRequest] set general values",
  readonly SET_APPOINTMENT_CONFIG: "[appointmentRequest] set appointment config",
  readonly SET_APPOINTMENTS: "[appointmentRequest] set appointments",
  readonly SET_ORGANIZER_INFO: "[appointmentRequest] set organizer info",
  readonly POST: "[appointmentRequest] post",
  readonly POST_SUCCESS: "[appointmentRequest] post success",
  readonly POST_ERROR: "[appointmentRequest] post error",
} = {
  SET_GENERAL_VALUES: "[appointmentRequest] set general values",
  SET_APPOINTMENT_CONFIG: "[appointmentRequest] set appointment config",
  SET_APPOINTMENTS: "[appointmentRequest] set appointments",
  SET_ORGANIZER_INFO: "[appointmentRequest] set organizer info",
  POST: "[appointmentRequest] post",
  POST_SUCCESS: "[appointmentRequest] post success",
  POST_ERROR: "[appointmentRequest] post error",
};

export class SetGeneralValuesAppointmentAction implements Action {
  readonly type = AppointmentActions.SET_GENERAL_VALUES;

  constructor(
    readonly payload: {
      title: string,
      description?: string,
      deadline: Date,
    }
  ) {
  }
}

export class SetAppointmentConfigAction implements Action {
  readonly type = AppointmentActions.SET_APPOINTMENT_CONFIG;

  constructor(
    readonly config: AppointmentConfig
  ) {
  }
}

export class SetAppointmentsAction implements Action {
  readonly type = AppointmentActions.SET_APPOINTMENTS;

  constructor(
    readonly appointments: AppointmentModel[]
  ) {
  }
}

export class SetOrganizerInfoAppointmentAction implements Action {
  readonly type = AppointmentActions.SET_ORGANIZER_INFO;

  constructor(
    readonly payload: {
      name?: string,
      email?: string,
      consigneeList: string[],
    }
  ) {
  }
}

export class PostAppointmentRequestAction implements Action {
  readonly type = AppointmentActions.POST;
}

export class PostAppointmentRequestSuccessAction implements Action {
  readonly type = AppointmentActions.POST_SUCCESS;

  constructor(
    readonly created: AppointmentRequestModel
  ) {
  }
}

export class PostAppointmentRequestErrorAction implements Action {
  readonly type = AppointmentActions.POST_ERROR;

  constructor(
    readonly error: any
  ) {
  }
}

export type AppointmentAction =
  SetGeneralValuesAppointmentAction
  | SetAppointmentConfigAction
  | SetAppointmentsAction
  | SetOrganizerInfoAppointmentAction
  | PostAppointmentRequestAction
  | PostAppointmentRequestSuccessAction
  | PostAppointmentRequestErrorAction;
