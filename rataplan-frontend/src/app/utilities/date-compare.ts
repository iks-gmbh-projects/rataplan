/**
 * Created by iho on 10.10.2017.
 */
export class DateCompare {

  public isSameDate(startDate: string, endDate: string) {

    let sDate = new Date(startDate);
    let eDate = new Date(endDate);

    return sDate.getFullYear() === eDate.getFullYear() && sDate.getMonth() === eDate.getMonth() && sDate.getDate() === eDate.getDate();
  }

  public isSameDateTime(startDate: string, endDate: string) {

    let sDate = new Date(startDate);
    let eDate = new Date(endDate);

    if (this.isSameDate(startDate, endDate)) {
      return sDate.getHours() === eDate.getHours() && sDate.getMinutes() === eDate.getMinutes();
    } else {
      return false;
    }
  }
}
