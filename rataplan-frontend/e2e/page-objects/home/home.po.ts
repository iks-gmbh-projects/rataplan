import { browser, by, element } from 'protractor';

export class HomePage {
  navigateTo() {
    return browser.get('/');
  }

  getParagraphText() {
    return element(by.css('rp-root h1')).getText();
  }

  getStartNowButton() {
    return element(by.css('.btn-primary')).getWebElement();
  }
}
