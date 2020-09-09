import { by, element } from 'protractor';

export class AppointmentMemberFormRowPage {

  private tag = '//tr[@rp-appointment-member-form-row]';
  private numberInput = 'rp-number-input input';

  public addAppointmentMemberButton = element(by.css(this.tag + ' .add-appointment-member-btn'));

  public clickAddAppointmentMember() {
    return element(by.xpath(this.tag)).element(by.css('.add-appointment-member-btn')).click();
  }

  public setDecisionParticipants(index: number, value: string) {
    // index + 2, da nth-child bei 1 anfängt und die erste spalte(td) den Namen beinhaltet. Die entscheidungen fangen erst in spalte 2 an, daher +2
    let numberInput = element(by.xpath(this.tag)).element(by.css('td:nth-child(' + (index + 2) + ') rp-number-input input'));
    numberInput.clear();
    numberInput.sendKeys(value);
  }

  public getDecisionParticipants(index: number) {
    return element(by.xpath(this.tag)).element(by.css('td:nth-child(' + (index + 2) + ') rp-number-input input')).getAttribute('value');
  }

  public setAppointmentMemberName(name: string) {
    let nameInput = element(by.xpath(this.tag)).element(by.css('.fix-left-col input'));
    nameInput.clear();
    nameInput.sendKeys(name);
  }

  public getAppointmentMemberName() {
    return element(by.xpath(this.tag)).element(by.css('.fix-left-col input')).getAttribute('value');
  }
}
