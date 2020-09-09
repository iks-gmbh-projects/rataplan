import { browser, by, element } from 'protractor';

export class AppointmentRequestLinkPage {

  private tag = 'rp-appointment-request-link';

  public titleH1 = element(by.css(this.tag + ' h1'));
  public titleH3 = element(by.css(this.tag + ' h3'));
  public titleH4 = element(by.css(this.tag + ' h4'));

  public getTitleH1() {
    return this.titleH1.getText();
  }

  public getTitleH3() {
    return this.titleH3.getText();
  }

  public getTitleH4() {
    return this.titleH4.getText();
  }

}
