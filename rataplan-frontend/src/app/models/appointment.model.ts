export class AppointmentModel {
  id: number | undefined;
  requestId: number | undefined;
  description: string | undefined;
  startDate: string | undefined;
  endDate: string | undefined;
  url: string | undefined;
  // displayedDescription: string | null;

  constructor() {
    // this.displayedDescription = 'null';
  }
}

export class AppointmentConfig {
  public startDate = true;
  public startTime = false;
  public endDate = false;
  public endTime = false;
  public url = false;
  public description = false;
}
