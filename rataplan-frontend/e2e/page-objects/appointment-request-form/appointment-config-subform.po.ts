import { by, element } from 'protractor';

export class AppointmentConfigFormPage {

  private tag = 'rp-appointment-config-subform';

  public descriptionCheckbox = element(by.css(this.tag + ' #description'));
  public urlCheckbox = element(by.css(this.tag + ' #url'));
  public startDateCheckbox = element(by.css(this.tag + ' #startDate'));
  public startTimeCheckbox = element(by.css(this.tag + ' #startTime'));
  public endDateCheckbox = element(by.css(this.tag + ' #endDate'));
  public endTimeCheckbox = element(by.css(this.tag + ' #endTime'));

  setDescriptionCheckbox(check) {
    this.descriptionCheckbox.isSelected().then((isSelected) => {
      if (isSelected !== check) {
        this.descriptionCheckbox.click();
      }
    });
  }

  getDescriptionCheckbox() {
    return this.descriptionCheckbox.isSelected();
  }

  setUrlCheckbox(check) {
    this.urlCheckbox.isSelected().then((isSelected) => {
      if (isSelected !== check) {
        this.urlCheckbox.click();
      }
    });
  }

  getUrlCheckbox() {
    return this.urlCheckbox.isSelected();
  }

  setStartDateCheckbox(check) {
    this.startDateCheckbox.isSelected().then((isSelected) => {
      if (isSelected !== check) {
        this.startDateCheckbox.click();
      }
    });
  }

  getStartDateCheckbox() {
    return this.startDateCheckbox.isSelected();
  }

  setStartTimeCheckbox(check) {
    this.startTimeCheckbox.isSelected().then((isSelected) => {
      if (isSelected !== check) {
        this.startTimeCheckbox.click();
      }
    });
  }

  getStartTimeCheckbox() {
    return this.startTimeCheckbox.isSelected();
  }

  setEndDateCheckbox(check) {
    this.endDateCheckbox.isSelected().then((isSelected) => {
      if (isSelected !== check) {
        this.endDateCheckbox.click();
      }
    });
  }

  getEndDateCheckbox() {
    return this.endDateCheckbox.isSelected();
  }

  setEndTimeCheckbox(check) {
    this.endTimeCheckbox.isSelected().then((isSelected) => {
      if (isSelected !== check) {
        this.endTimeCheckbox.click();
      }
    });
  }

  getEndTimeCheckbox() {
    return this.endTimeCheckbox.isSelected();
  }

}
