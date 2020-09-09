import { by, element } from 'protractor';

export class AppointmentListPage {

  private tag = 'rp-appointment-list';

  public appointmentList = element.all(by.css(this.tag + ' #appointment-list'));

  clickDeleteButton(index) {
    return element.all(by.css(this.tag + ' .appointment-delete')).get(index).click();
  }

  getStartDate(index) {
    index++;
    return element(by.css(this.tag + ' .appointment-borders:nth-child(' + index + ') #showStartDate')).getText();
  }

  getEndDate(index) {
    index++;
    return element(by.css(this.tag + ' .appointment-borders:nth-child(' + index + ') #showEndDate')).getText();
  }

  getDescription(index) {
    index++;
    return element(by.css(this.tag + ' .appointment-borders:nth-child(' + index + ') #showDescription')).getText();
  }

  getUrl(index) {
    index++;
    return element(by.css(this.tag + ' .appointment-borders:nth-child(' + index + ') #showUrl')).getText();
  }

  getAppointmentList() {
    return this.appointmentList;
  }

  getAppointmentWithIndex(index) {
    return element.all(by.css(this.tag + ' .appointment-borders')).get(index);
  }

}

