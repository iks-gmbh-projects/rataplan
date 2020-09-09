import { browser, by, element } from 'protractor';

export class ShowAppointmentRequestPage {

  private tag = 'rp-show-appointment-request';

  public nameInput = element(by.css(this.tag + ' #member-name'));
  public editNameInput = element(by.css(this.tag + ' #edit-name'));
  public addButton = element(by.css(this.tag + ' .add-appointment-member-btn'));

  navigateTo(id) {
    return browser.get('/appointmentrequest/' + id);
  }

  setNewMemberName(name) {
    this.nameInput.clear();
    this.nameInput.sendKeys(name);
  }

  getNewMemberName() {
    return this.nameInput.getAttribute('value');
  }

  getMemberWithIndex(index) {
    return element.all(by.css('.appointment-member-name')).get(index);
  }

  getMemberList() {
    return element.all(by.css('.appointment-member-name'));
  }

  // getDecision(memberIndex, decisionIndex) {  }

  setEditMemberName(name) {
    this.editNameInput.clear();
    this.editNameInput.sendKeys(name);
  }

  getEditMemberName() {
    return this.editNameInput.getAttribute('value');
  }

  clickAddMemberButton() {
    return this.addButton.click();
  }

  clickEditMemberButton(index) {
    element.all(by.css(this.tag + ' .glyphicon-edit')).get(index).click();
  }

  clickSaveEditMemberButton() {
    element.all(by.css(this.tag + ' .glyphicon-floppy-disk')).get(1).click();
  }

  clickCancelEditMemberButton() {
    element(by.css(this.tag + ' .glyphicon-remove')).click();
  }

  public getAppointmentMemberRows() {
    return element.all(by.css(this.tag + ' tr[rp-show-appointment-member-row]'));
  }

  public getAppointmentMemberWithIndex(index: number) {
    return element.all(by.css(this.tag + ' tr[rp-show-appointment-member-row]')).get(index);
  }

}
