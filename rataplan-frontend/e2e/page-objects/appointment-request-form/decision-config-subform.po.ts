import { by, element } from 'protractor';

export class DecisionConfigFormPage {

  private tag = 'rp-decision-config-subform';

  public defaultRadio = element(by.css(this.tag + ' #defaultConfig'));
  public extendedRadio = element(by.css(this.tag + ' #extendedConfig'));
  public numberRadio = element(by.css(this.tag + ' #numberConfig'));

  setDefaultRadio(check) {
    this.defaultRadio.isSelected().then((isSelected) => {
      if (isSelected !== check) {
        this.defaultRadio.click();
      }
    });
  }

  getDefaultRadio() {
    return this.defaultRadio.isSelected();
  }

  setExtendedRadio(check) {
    this.extendedRadio.isSelected().then((isSelected) => {
      if (isSelected !== check) {
        this.extendedRadio.click();
      }
    });
  }

  getExtendedRadio() {
    return this.extendedRadio.isSelected();
  }

  setNumberRadio(check) {
    this.numberRadio.isSelected().then((isSelected) => {
      if (isSelected !== check) {
        this.numberRadio.click();
      }
    });
  }

  getNumberRadio() {
    return this.numberRadio.isSelected();
  }

}
