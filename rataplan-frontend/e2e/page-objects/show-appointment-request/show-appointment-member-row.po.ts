import { by, element } from 'protractor';

export class ShowAppointmentMemberRowPage {
  private tag = '//tr[@rp-show-appointment-member-row]';

  public getDisplayedAppointmentMemberName(index) {
    return element.all(by.xpath(this.tag)).get(index).element(by.css('td:nth-child(1)')).getText();
  }

  public getDecisionParticipants(rowIndex, columnIndex) {
    return element.all(by.xpath(this.tag)).get(rowIndex).element(by.css('td:nth-child(' + (columnIndex + 2) + ')')).getText();
  }

  public clickEditAppointmentMember(index) {
    return element.all(by.xpath(this.tag)).get(index).element(by.css('.glyphicon-edit')).click();
  }

  public clickSaveAppointmentMember(index) {
    return element.all(by.xpath(this.tag)).get(index).element(by.css('.glyphicon-floppy-disk')).click();
  }

  public clickCancelAppointmentMember(index) {
    return element.all(by.xpath(this.tag)).get(index).element(by.css('.glyphicon-remove')).click();
  }

  public setEditAppointmentMemberMame(rowIndex: number, name: string) {
    let nameInput = element.all(by.xpath(this.tag)).get(rowIndex).element(by.css('.fix-left-col input'));
    nameInput.clear();
    nameInput.sendKeys(name);
  }

  public getEditAppointmentMemberName(rowIndex: number) {
    return element.all(by.xpath(this.tag)).get(rowIndex).element(by.css('.fix-left-col input')).getAttribute('value');
  }

  public setEditDecisionParticipants(rowIndex: number, columnIndex: number, value: string) {
    // index + 2, da nth-child bei 1 anfängt und die erste spalte(td) den Namen beinhaltet. Die entscheidungen fangen erst in spalte 2 an, daher +2
    let numberInput = element.all(by.xpath(this.tag)).get(rowIndex).element(by.css('td:nth-child(' + (columnIndex + 2) + ') rp-number-input input'));
    numberInput.clear();
    numberInput.sendKeys(value);
  }

  public getEditDecisionParticipants(rowIndex: number, columndIndex: number) {
    return element.all(by.xpath(this.tag)).get(rowIndex).element(by.css('td:nth-child(' + (columndIndex + 2) + ') rp-number-input input')).getAttribute('value');
  }

}
