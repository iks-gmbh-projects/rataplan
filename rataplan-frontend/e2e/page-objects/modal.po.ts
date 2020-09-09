import { browser, by, element } from 'protractor';

export class ModalPage {

  private tag = 'modal';

  public modal = element(by.css(this.tag));

  getModal() {
    return element.all(by.css(this.tag)).get(0);
  }
}
