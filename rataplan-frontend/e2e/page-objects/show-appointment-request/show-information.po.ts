import { by, element } from 'protractor';

export class ShowInformationPage {

  private tag = 'rp-show-information';

  public getTitle() {
    return element(by.css(this.tag + ' h1 u')).getText();
  }

  public getDescription() {
    return element(by.css(this.tag + ' .description-headline')).getText();
  }

  public getDeadline() {
    return element(by.css(this.tag + ' .deadline')).getText();
  }

}
