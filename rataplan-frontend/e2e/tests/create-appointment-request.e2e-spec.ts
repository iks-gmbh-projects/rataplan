import { browser, protractor, ProtractorExpectedConditions } from 'protractor';
import { AppointmentConfigFormPage } from '../page-objects/appointment-request-form/appointment-config-subform.po';
import { AppointmentListPage } from '../page-objects/appointment-request-form/appointment-list.po';
import { AppointmentRequestCreatePage } from '../page-objects/appointment-request-form/appointment-request-create.po';
import { AppointmentRequestFormPage } from '../page-objects/appointment-request-form/appointment-request-form.po';
import { AppointmentSubformPage } from '../page-objects/appointment-request-form/appointment-subform.po';
import { DecisionConfigFormPage } from '../page-objects/appointment-request-form/decision-config-subform.po';
import { GeneralSubformPage } from '../page-objects/appointment-request-form/general-subform.po';
import { ModalPage } from '../page-objects/modal.po';
import { AppointmentRequestLinkPage } from '../page-objects/appointment-request-form/appointment-request-link.po';
import { SecuritySubformPage } from '../page-objects/appointment-request-form/security-subform.po';


// Funktioniert zur Zeit aufgrund von Zeitzonen nicht
xdescribe('create appointmentRequest', () => {
  let appointmentRequestCreatePage: AppointmentRequestCreatePage;
  let appointmentConfigFormPage: AppointmentConfigFormPage;
  let appointmentListPage: AppointmentListPage;
  let appointmenRequestFormPage: AppointmentRequestFormPage;
  let appointmentSubformPage: AppointmentSubformPage;
  let decisionConfigFormPage: DecisionConfigFormPage;
  let generalSubformPage: GeneralSubformPage;
  let appointmentRequestLinkPage: AppointmentRequestLinkPage;
  let modalPage: ModalPage;
  let securitySubformPage: SecuritySubformPage;

  let until: ProtractorExpectedConditions;

  beforeEach(() => {
    appointmentRequestCreatePage = new AppointmentRequestCreatePage();
    appointmentConfigFormPage = new AppointmentConfigFormPage();
    appointmentListPage = new AppointmentListPage();
    appointmenRequestFormPage = new AppointmentRequestFormPage();
    appointmentSubformPage = new AppointmentSubformPage();
    decisionConfigFormPage = new DecisionConfigFormPage();
    generalSubformPage = new GeneralSubformPage();
    appointmentRequestLinkPage = new AppointmentRequestLinkPage();
    modalPage = new ModalPage();
    securitySubformPage = new SecuritySubformPage();
    until = protractor.ExpectedConditions;
  });

  it('should create appointmentRequest with two appointments', () => {
    appointmentRequestCreatePage.navigateTo();

    generalSubformPage.setTitle('Sommerfest');
    generalSubformPage.setDescription('Sommerfest 2050');
    generalSubformPage.setOrganizerMail('');
    generalSubformPage.setDeadline('01.01.2050');

    decisionConfigFormPage.setDefaultRadio(true);

    expect(appointmenRequestFormPage.getHeadingH1()).toBe('Terminanfrage erstellen');
    expect(generalSubformPage.getTitle()).toBe('Sommerfest');
    expect(generalSubformPage.getDescription()).toBe('Sommerfest 2050');
    expect(generalSubformPage.getOrganizerMail()).toBe('');
    expect(generalSubformPage.getDeadline()).toBe('01.01.2050');

    securitySubformPage.secureByPassword();
    securitySubformPage.setAdminPassword('test');

    appointmenRequestFormPage.clickNextButton();
    expect(appointmenRequestFormPage.getHeadingH2()).toBe('Sommerfest');

    expect(appointmentConfigFormPage.getDescriptionCheckbox()).toBe(true);
    expect(appointmentConfigFormPage.getUrlCheckbox()).toBe(false);
    expect(appointmentConfigFormPage.getStartDateCheckbox()).toBe(false);
    expect(appointmentConfigFormPage.getStartTimeCheckbox()).toBe(false);
    expect(appointmentConfigFormPage.getEndDateCheckbox()).toBe(false);
    expect(appointmentConfigFormPage.getEndTimeCheckbox()).toBe(false);

    appointmentSubformPage.getUrlWrapper().getCssValue('display').then((attr) => {
      expect(attr).toBe('none');
    });

    appointmentSubformPage.getStartDateWrapper().getCssValue('display').then((attr) => {
      expect(attr).toBe('none');
    });

    appointmentSubformPage.getStartTimeWrapper().getCssValue('display').then((attr) => {
      expect(attr).toBe('none');
    });

    appointmentSubformPage.getEndDateWrapper().getCssValue('display').then((attr) => {
      expect(attr).toBe('none');
    });

    appointmentSubformPage.getEndTimeWrapper().getCssValue('display').then((attr) => {
      expect(attr).toBe('none');
    });

    appointmentConfigFormPage.setUrlCheckbox(false);
    appointmentConfigFormPage.setStartDateCheckbox(true);
    appointmentConfigFormPage.setStartTimeCheckbox(true);
    appointmentConfigFormPage.setEndDateCheckbox(true);
    appointmentConfigFormPage.setEndTimeCheckbox(true);

    expect(appointmentConfigFormPage.getDescriptionCheckbox()).toBe(true);
    expect(appointmentConfigFormPage.getUrlCheckbox()).toBe(false);
    expect(appointmentConfigFormPage.getStartDateCheckbox()).toBe(true);
    expect(appointmentConfigFormPage.getStartTimeCheckbox()).toBe(true);
    expect(appointmentConfigFormPage.getEndDateCheckbox()).toBe(true);
    expect(appointmentConfigFormPage.getEndTimeCheckbox()).toBe(true);

    appointmentSubformPage.getUrlWrapper().getCssValue('display').then((attr) => {
      expect(attr).toBe('none');
    });

    appointmentConfigFormPage.setEndTimeCheckbox(false);
    expect(appointmentConfigFormPage.getEndTimeCheckbox()).toBe(false);
    appointmentSubformPage.getEndTimeWrapper().getCssValue('display').then((attr) => {
      expect(attr).toBe('none');
    });

    appointmentConfigFormPage.setEndTimeCheckbox(true);
    expect(appointmentConfigFormPage.getEndTimeCheckbox()).toBe(true);
    appointmentListPage.getAppointmentList().then(list => {
      expect(list.length).toBe(0);
    });

    appointmentSubformPage.setDescription('Erster Termin');
    appointmentSubformPage.setStartDate('09.09.2050');
    appointmentSubformPage.setStartTime('1000');
    appointmentSubformPage.setEndDate('16.09.2050');
    appointmentSubformPage.setEndTime('1600');

    expect(appointmentSubformPage.getDescription()).toBe('Erster Termin');
    expect(appointmentSubformPage.getStartDate()).toBe('09.09.2050');
    expect(appointmentSubformPage.getStartTime()).toBe('1000');
    expect(appointmentSubformPage.getEndDate()).toBe('16.09.2050');
    expect(appointmentSubformPage.getEndTime()).toBe('1600');

    appointmentSubformPage.clickAddButton();
    browser.waitForAngular();

    browser.wait(until.visibilityOf(appointmentListPage.getAppointmentWithIndex(0)), 10000, 'Adding appointment failed');

    appointmentListPage.getAppointmentList().then(list => {
      expect(list.length).toBe(1);
    });

    expect(appointmentListPage.getDescription(0)).toBe('Erster Termin');
    expect(appointmentListPage.getStartDate(0)).toBe('09.09.2050 10:00 Uhr');
    expect(appointmentListPage.getEndDate(0)).toBe('- 16.09.2050 16:00 Uhr');

    appointmentSubformPage.setDescription('Zweiter Termin');
    appointmentSubformPage.setStartDate('10.10.2050');
    appointmentSubformPage.setStartTime('1000');
    appointmentSubformPage.setEndDate('16.10.2050');
    appointmentSubformPage.setEndTime('1600');

    expect(appointmentSubformPage.getDescription()).toBe('Zweiter Termin');
    expect(appointmentSubformPage.getStartDate()).toBe('10.10.2050');
    expect(appointmentSubformPage.getStartTime()).toBe('1000');
    expect(appointmentSubformPage.getEndDate()).toBe('16.10.2050');
    expect(appointmentSubformPage.getEndTime()).toBe('1600');

    appointmentSubformPage.clickAddButton();

    browser.wait(until.visibilityOf(appointmentListPage.getAppointmentWithIndex(1)), 10000, 'Adding appointment failed');
    browser.wait(until.visibilityOf(appointmentListPage.getAppointmentWithIndex(0)), 10000, 'Adding appointment failed');

    appointmentListPage.getAppointmentList().then(list => {
      expect(list.length).toBe(2);
    });

    expect(appointmentListPage.getDescription(0)).toBe('Zweiter Termin');
    expect(appointmentListPage.getStartDate(0)).toBe('10.10.2050 10:00 Uhr');
    expect(appointmentListPage.getEndDate(0)).toBe('- 16.10.2050 16:00 Uhr');

    expect(appointmentListPage.getDescription(1)).toBe('Erster Termin');
    expect(appointmentListPage.getStartDate(1)).toBe('09.09.2050 10:00 Uhr');
    expect(appointmentListPage.getEndDate(1)).toBe('- 16.09.2050 16:00 Uhr');

    appointmenRequestFormPage.clickBackButton();

    expect(appointmenRequestFormPage.getHeadingH1()).toBe('Terminanfrage erstellen');
    expect(generalSubformPage.getTitle()).toBe('Sommerfest');
    expect(generalSubformPage.getDescription()).toBe('Sommerfest 2050');
    expect(generalSubformPage.getOrganizerMail()).toBe('');
    expect(generalSubformPage.getDeadline()).toBe('01.01.2050');

    appointmenRequestFormPage.clickNextButton();

    expect(appointmenRequestFormPage.getHeadingH2()).toBe('Sommerfest');

    expect(appointmentConfigFormPage.getDescriptionCheckbox()).toBe(true);
    expect(appointmentConfigFormPage.getUrlCheckbox()).toBe(false);
    expect(appointmentConfigFormPage.getStartDateCheckbox()).toBe(true);
    expect(appointmentConfigFormPage.getStartTimeCheckbox()).toBe(true);
    expect(appointmentConfigFormPage.getEndDateCheckbox()).toBe(true);
    expect(appointmentConfigFormPage.getEndTimeCheckbox()).toBe(true);

    appointmentSubformPage.getUrlWrapper().getCssValue('display').then((attr) => {
      expect(attr).toBe('none');
    });

    appointmentListPage.getAppointmentList().then(list => {
      expect(list.length).toBe(2);
    });

    expect(appointmentListPage.getDescription(0)).toBe('Zweiter Termin');
    expect(appointmentListPage.getStartDate(0)).toBe('10.10.2050 10:00 Uhr');
    expect(appointmentListPage.getEndDate(0)).toBe('- 16.10.2050 16:00 Uhr');

    expect(appointmentListPage.getDescription(1)).toBe('Erster Termin');
    expect(appointmentListPage.getStartDate(1)).toBe('09.09.2050 10:00 Uhr');
    expect(appointmentListPage.getEndDate(1)).toBe('- 16.09.2050 16:00 Uhr');

    appointmentSubformPage.setDescription('Dritter Termin');
    appointmentSubformPage.setStartDate('08.08.2050');
    appointmentSubformPage.setStartTime('1000');
    appointmentSubformPage.setEndDate('16.08.2050');
    appointmentSubformPage.setEndTime('1600');

    expect(appointmentSubformPage.getDescription()).toBe('Dritter Termin');
    expect(appointmentSubformPage.getStartDate()).toBe('08.08.2050');
    expect(appointmentSubformPage.getStartTime()).toBe('1000');
    expect(appointmentSubformPage.getEndDate()).toBe('16.08.2050');
    expect(appointmentSubformPage.getEndTime()).toBe('1600');

    appointmentSubformPage.clickAddButton();

    browser.wait(until.visibilityOf(appointmentListPage.getAppointmentWithIndex(2)), 10000, 'Adding appointment failed');
    browser.wait(until.visibilityOf(appointmentListPage.getAppointmentWithIndex(0)), 10000, 'Adding appointment failed');

    appointmentListPage.getAppointmentList().then(list => {
      expect(list.length).toBe(3);
    });

    expect(appointmentListPage.getDescription(0)).toBe('Dritter Termin');
    expect(appointmentListPage.getStartDate(0)).toBe('08.08.2050 10:00 Uhr');
    expect(appointmentListPage.getEndDate(0)).toBe('- 16.08.2050 16:00 Uhr');

    expect(appointmentListPage.getDescription(1)).toBe('Zweiter Termin');
    expect(appointmentListPage.getStartDate(1)).toBe('10.10.2050 10:00 Uhr');
    expect(appointmentListPage.getEndDate(1)).toBe('- 16.10.2050 16:00 Uhr');

    expect(appointmentListPage.getDescription(2)).toBe('Erster Termin');
    expect(appointmentListPage.getStartDate(2)).toBe('09.09.2050 10:00 Uhr');
    expect(appointmentListPage.getEndDate(2)).toBe('- 16.09.2050 16:00 Uhr');

    appointmentListPage.clickDeleteButton(0);

    browser.wait(until.invisibilityOf(appointmentListPage.getAppointmentWithIndex(2)), 10000, 'Deleting appointment failed');

    appointmentListPage.getAppointmentList().then(list => {
      expect(list.length).toBe(2);
    });

    expect(appointmentListPage.getDescription(0)).toBe('Zweiter Termin');
    expect(appointmentListPage.getStartDate(0)).toBe('10.10.2050 10:00 Uhr');
    expect(appointmentListPage.getEndDate(0)).toBe('- 16.10.2050 16:00 Uhr');

    expect(appointmentListPage.getDescription(1)).toBe('Erster Termin');
    expect(appointmentListPage.getStartDate(1)).toBe('09.09.2050 10:00 Uhr');
    expect(appointmentListPage.getEndDate(1)).toBe('- 16.09.2050 16:00 Uhr');

    appointmenRequestFormPage.clickSubmitButton();

    browser.wait(until.visibilityOf(modalPage.getModal()), 10000, 'Modal did not open');

    browser.waitForAngular();

    browser.waitForAngular();
    expect(appointmentRequestLinkPage.getTitleH1()).toEqual('Geschafft!');
    expect(appointmentRequestLinkPage.getTitleH3()).toEqual('Unter dem folgenden Link können nun alle Teilnehmer abstimmen:');
    expect(appointmentRequestLinkPage.getTitleH4()).toEqual('Falls Sie die Terminanfrage bearbeiten möchten, können Sie dies unter folgendem Link tun:');
  });

});
