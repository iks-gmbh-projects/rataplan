import { browser, by, element } from 'protractor';

export class AppointmentRequestCreatePage {

  private tag = 'rp-appointment-request-create';

  public modal = element(by.css(this.tag + ' modal'));

  navigateTo() {
    return browser.get('/appointmentrequest');
  }

  getModal() {
    return this.modal;
  }

}
