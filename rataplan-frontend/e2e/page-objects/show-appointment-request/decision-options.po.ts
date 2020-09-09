import { by, element } from 'protractor';

export class DecisionOptionsPage {

  private tag = 'rp-decision-options';

  clickSelectBox(index) {
    return element.all(by.css('select')).get(index).click();
  }

  chooseOption(index) {
    element.all(by.css('option')).get(index).click();
  }

  getOptionText(index) {
    return element.all(by.css(this.tag)).get(index).getText();
  }

  options(index) {
    return element.all(by.css('option')).get(index);
  }
}
