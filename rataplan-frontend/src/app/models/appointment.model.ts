export class AppointmentModel {
  id: number | undefined;
  requestId: number | undefined;
  description: string | undefined;
  startDate: string | undefined;
  startTime: string | undefined;
  endDate: string | undefined;
  endTime: string | undefined;
  url: string | undefined;
  // displayedDescription: string | null;

  constructor() {
    // this.displayedDescription = 'null';
  }
}

export class AppointmentConfig {
  public startDate = true;
  public startTime = true;
  public endDate = false;
  public endTime = false;
  public url = false;
  public description = false;
}
