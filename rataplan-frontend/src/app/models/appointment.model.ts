export type AppointmentModel = {
  id?: number,
  requestId?: number,
  description?: string,
  startDate?: string,
  endDate?: string,
  url?: string,
};

export class AppointmentConfig {
  public startDate = true;
  public startTime = false;
  public endDate = false;
  public endTime = false;
  public url = false;
  public description = false;
}
