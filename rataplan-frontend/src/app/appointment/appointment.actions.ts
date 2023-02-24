import { Action } from "@ngrx/store";
import { AppointmentConfig, AppointmentModel } from "../models/appointment.model";
import { AppointmentRequestModel } from "../models/appointment-request.model";
import { DecisionType } from "./appointment-request-form/decision-type.enum";

export const AppointmentActions: {
  readonly INIT: "[appointmentRequest] init",
  readonly INIT_SUCCESS: "[appointmentRequest] init success",
  readonly INIT_ERROR: "[appointmentRequest] init error",
  readonly SET_GENERAL_VALUES: "[appointmentRequest] set general values",
  readonly SET_APPOINTMENT_CONFIG: "[appointmentRequest] set appointment config",
  readonly SET_APPOINTMENTS: "[appointmentRequest] set appointments",
  readonly ADD_APPOINTMENTS: "[appointmentRequest] add appointments",
  readonly REMOVE_APPOINTMENT: "[appointmentRequest] remove appointment",
  readonly SET_ORGANIZER_INFO: "[appointmentRequest] set organizer info",
  readonly POST: "[appointmentRequest] post",
  readonly POST_SUCCESS: "[appointmentRequest] post success",
  readonly POST_ERROR: "[appointmentRequest] post error",
} = {
  INIT: "[appointmentRequest] init",
  INIT_SUCCESS: "[appointmentRequest] init success",
  INIT_ERROR: "[appointmentRequest] init error",
  SET_GENERAL_VALUES: "[appointmentRequest] set general values",
  SET_APPOINTMENT_CONFIG: "[appointmentRequest] set appointment config",
  SET_APPOINTMENTS: "[appointmentRequest] set appointments",
  ADD_APPOINTMENTS: "[appointmentRequest] add appointments",
  REMOVE_APPOINTMENT: "[appointmentRequest] remove appointment",
  SET_ORGANIZER_INFO: "[appointmentRequest] set organizer info",
  POST: "[appointmentRequest] post",
  POST_SUCCESS: "[appointmentRequest] post success",
  POST_ERROR: "[appointmentRequest] post error",
};

export class InitAppointmentRequestAction implements Action {
  readonly type = AppointmentActions.INIT;
  constructor(
    readonly id?: string | number
  ) {
  }
}

export class InitAppointmentRequestSuccessAction implements Action {
  readonly type = AppointmentActions.INIT_SUCCESS;
  constructor(
    readonly request: AppointmentRequestModel
  ) {
  }
}

export class InitAppointmentRequestErrorAction implements Action {
  readonly type = AppointmentActions.INIT_ERROR;
  constructor(
    readonly error: any
  ) {
  }
}

export class SetGeneralValuesAppointmentAction implements Action {
  readonly type = AppointmentActions.SET_GENERAL_VALUES;

  constructor(
    readonly payload: {
      title: string,
      description?: string,
      deadline: Date,
      decisionType: DecisionType,
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

export class AddAppointmentsAction implements Action {
  readonly type = AppointmentActions.ADD_APPOINTMENTS;
  readonly appointments: AppointmentModel[];
  constructor(
    ...appointments: AppointmentModel[]
  ) {
    this.appointments = appointments;
  }
}

export class RemoveAppointmentAction implements Action {
  readonly type = AppointmentActions.REMOVE_APPOINTMENT;
  constructor(
    readonly index: number
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
    readonly created: AppointmentRequestModel,
    readonly editToken?: string,
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
  InitAppointmentRequestAction
  | InitAppointmentRequestSuccessAction
  | InitAppointmentRequestErrorAction
  | SetGeneralValuesAppointmentAction
  | SetAppointmentConfigAction
  | SetAppointmentsAction
  | AddAppointmentsAction
  | RemoveAppointmentAction
  | SetOrganizerInfoAppointmentAction
  | PostAppointmentRequestAction
  | PostAppointmentRequestSuccessAction
  | PostAppointmentRequestErrorAction;
