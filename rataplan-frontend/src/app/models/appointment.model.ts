export type AppointmentModel = {
  id?: number,
  requestId?: number,
  description?: string,
  startDate?: string,
  endDate?: string,
  url?: string,
};

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
