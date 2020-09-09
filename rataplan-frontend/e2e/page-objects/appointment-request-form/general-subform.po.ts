import { by, element } from 'protractor';

export class GeneralSubformPage {

  private tag = 'rp-general-subform';

  public titleInput = element(by.css(this.tag + ' #title'));
  public descriptionInput = element(by.css(this.tag + ' #description'));
  public organizerMailInput = element(by.css(this.tag + ' #email'));
  public deadlineInput = element(by.css(this.tag + ' #deadline input'));

  setTitle(title: string) {
    this.titleInput.clear();
    this.titleInput.sendKeys(title);
  }

  getTitle() {
    return this.titleInput.getAttribute('value');
  }

  setDescription(description: string) {
    this.descriptionInput.clear();
    this.descriptionInput.sendKeys(description);
  }

  getDescription() {
    return this.descriptionInput.getAttribute('value');
  }

  setOrganizerMail(organizerMail: string) {
    this.organizerMailInput.clear();
    this.organizerMailInput.sendKeys(organizerMail);
  }

  getOrganizerMail() {
    return this.organizerMailInput.getAttribute('value');
  }

  setDeadline(deadline) {
    this.deadlineInput.clear();
    this.deadlineInput.sendKeys(deadline);
  }

  getDeadline() {
    return this.deadlineInput.getAttribute('value');
  }

}
