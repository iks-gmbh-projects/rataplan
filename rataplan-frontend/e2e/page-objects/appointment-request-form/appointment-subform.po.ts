import { by, element } from 'protractor';

export class AppointmentSubformPage {

  private tag = 'rp-appointment-subform';

  public descriptionInput = element(by.css(this.tag + ' #description'));
  public urlInput = element(by.css(this.tag + ' #url'));
  public startDateInput = element(by.css(this.tag + ' #startDate input'));
  public startTimeInput = element(by.css(this.tag + ' #startTime'));
  public endDateInput = element(by.css(this.tag + ' #endDate input'));
  public endTimeInput = element(by.css(this.tag + ' #endTime'));

  public descriptionWrapper = element(by.css(this.tag + ' #description-wrapper'));
  public urlWrapper = element(by.css(this.tag + ' #url-wrapper'));
  public startDateWrapper = element(by.css(this.tag + ' #startDate-wrapper'));
  public startTimeWrapper = element(by.css(this.tag + ' #startTime-wrapper'));
  public endDateWrapper = element(by.css(this.tag + ' #endDate-wrapper'));
  public endTimeWrapper = element(by.css(this.tag + ' #endTime-wrapper'));

  public addButton = element(by.css(this.tag + ' #btn-add'));

  setDescription(description) {
    this.descriptionInput.clear();
    this.descriptionInput.sendKeys(description);
  }

  getDescription() {
    return this.descriptionInput.getAttribute('value');
  }

  setUrl(url) {
    this.urlInput.clear();
    this.urlInput.sendKeys(url);
  }

  getUrl() {
    return this.urlInput.getAttribute('value');
  }

  setStartDate(startDate) {
    this.startDateInput.clear();
    this.startDateInput.sendKeys(startDate);
  }

  getStartDate() {
    return this.startDateInput.getAttribute('value');
  }

  setStartTime(startTime) {
    this.startTimeInput.clear();
    this.startTimeInput.sendKeys(startTime);
  }

  getStartTime() {
    return this.startTimeInput.getAttribute('value');
  }

  setEndDate(endDate) {
    this.endDateInput.clear();
    this.endDateInput.sendKeys(endDate);
  }

  getEndDate() {
    return this.endDateInput.getAttribute('value');
  }

  setEndTime(endTime) {
    this.endTimeInput.clear();
    this.endTimeInput.sendKeys(endTime);
  }

  getEndTime() {
    return this.endTimeInput.getAttribute('value');
  }

  getDescriptionWrapper() {
    return this.descriptionWrapper;
  }

  getUrlWrapper() {
    return this.urlWrapper;
  }

  getStartDateWrapper() {
    return this.startDateWrapper;
  }

  getStartTimeWrapper() {
    return this.startTimeWrapper;
  }

  getEndDateWrapper() {
    return this.endDateWrapper;
  }

  getEndTimeWrapper() {
    return this.endTimeWrapper;
  }

  clickAddButton() {
    return this.addButton.click();
  }

}
