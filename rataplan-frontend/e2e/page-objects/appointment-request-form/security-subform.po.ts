/**
 * Created by iho on 16.10.2017.
 */
import { by, element } from 'protractor';

export class SecuritySubformPage {
  private tag = 'rp-security-subform';

  public adminPasswordInput = element(by.css(this.tag + ' #admin-password'));
  public userPasswordInput = element(by.css(this.tag + ' #password'));
  public passwordCheckbox = element(by.css(this.tag + ' input'));

  secureByPassword() {
    this.passwordCheckbox.click();
  }

  setAdminPassword(password: string) {
    this.adminPasswordInput.clear();
    this.adminPasswordInput.sendKeys(password);
  }

  setUserPassword(password: string) {
    this.userPasswordInput.clear();
    this.userPasswordInput.sendKeys(password);
  }
}
