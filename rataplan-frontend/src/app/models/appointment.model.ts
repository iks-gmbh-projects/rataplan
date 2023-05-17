export type AppointmentModel<serialized extends boolean = false> = {
  id?: number,
  requestId?: number,
  description?: string,
  startDate?: serialized extends false ? string : (string|number),
  endDate?: serialized extends false ? string : (string|number),
  url?: string,
};

export function deserializeAppointmentModel(appointment: AppointmentModel<boolean>): AppointmentModel {
  if(appointment.startDate) appointment.startDate = new Date(appointment.startDate).toISOString();
  if(appointment.endDate) appointment.endDate = new Date(appointment.endDate).toISOString();
  return appointment as AppointmentModel;
}

export type AppointmentConfig = {
  startDate?: boolean,
  startTime?: boolean,
  endDate?: boolean,
  endTime?: boolean,
  url?: boolean,
  description?: boolean,
}

export function isConfiguredEqual(a: AppointmentConfig, b: AppointmentConfig): boolean {
  if(a == b) return true;
  if("object" !== typeof a) return false;
  if("object" !== typeof b) return false;
  return !a.startDate == !b.startDate &&
    !a.startTime == !b.startTime &&
    !a.endDate == !b.endDate &&
    !a.endTime == !b.endTime &&
    !a.description == !b.description &&
    !a.url == !b.url;
}

export function matchesConfiguration(a: AppointmentModel, config: AppointmentConfig): boolean {
  return ((config.startDate || config.startTime) == !!a.startDate) &&
    ((config.endDate || config.endTime) == !!a.endDate) &&
    (config.description == !!a.description) &&
    (config.url == !!a.url);
}

export function matchConfiguration(a: AppointmentModel[], config: AppointmentConfig): boolean {
  return a.every(a => matchesConfiguration(a, config));
}
