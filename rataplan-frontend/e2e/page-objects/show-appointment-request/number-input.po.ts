import { by, element } from 'protractor';

export class NumberInputPage {

  private tag = 'rp-number-input';

  setNumberInput(index, value) {
    let numberInput = element.all(by.css(this.tag + ' input')).get(index);
    numberInput.clear();
    numberInput.sendKeys(value);
  }

  getNumberInput(index) {
    return element.all(by.css(this.tag + ' input')).get(index).getAttribute('value');
  }

}
