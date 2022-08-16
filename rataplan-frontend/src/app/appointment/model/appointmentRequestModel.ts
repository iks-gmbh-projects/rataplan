export class AppointmentRequestModel {

  constructor(
    public title?: string,
    public description?: string,
    public deadline?: Date,
    public decision?: string,
    public selectedDates?: Date[],
    public creatorName?: string,
    public creatorEmail?: string,
  ) {
  }

}
