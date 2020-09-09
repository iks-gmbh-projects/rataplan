import { by, element } from 'protractor';

export class AppointmentRequestFormPage {

  private tag = 'rp-appointment-request-form';

  public headingH1 = element(by.css(this.tag + ' h1'));
  public headingH2 = element(by.css(this.tag + ' h2'));
  public nextButton = element(by.css(this.tag + ' #btn-next'));
  public cancelButton = element(by.css(this.tag + ' #btn-cancel'));
  public submitButton = element(by.css(this.tag + ' #btn-submit'));
  public backButton = element(by.css(this.tag + ' #btn-back'));

  getHeadingH1() {
    return this.headingH1.getText();
  }

  getHeadingH2() {
    return this.headingH2.getText();
  }

  clickCancelButton() {
    return this.cancelButton.click();
  }

  clickNextButton() {
    return this.nextButton.click();
  }

  clickSubmitButton() {
    return this.submitButton.click();
  }

  clickBackButton() {
    return this.backButton.click();
  }

}
