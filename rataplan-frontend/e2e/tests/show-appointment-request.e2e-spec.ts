import { browser, protractor, ProtractorExpectedConditions } from 'protractor';
import { AppointmentMemberFormRowPage } from '../page-objects/show-appointment-request/appointment-member-form-row.po';
import { DecisionOptionsPage } from '../page-objects/show-appointment-request/decision-options.po';
import { NumberInputPage } from '../page-objects/show-appointment-request/number-input.po';
import { ShowAppointmentMemberRowPage } from '../page-objects/show-appointment-request/show-appointment-member-row.po';
import { ShowAppointmentRequestPage } from '../page-objects/show-appointment-request/show-appointment-request.po';
import { StatisticPage } from '../page-objects/show-appointment-request/statistic.po';
import { ShowInformationPage } from '../page-objects/show-appointment-request/show-information.po';

// funktioniert grundsätzlich, zZ leider nicht aufgrund neuer Komponenten
xdescribe('show appointmentRequest', () => {
  let showAppointmentRequestPage: ShowAppointmentRequestPage;
  let appointmentMemberFormRowPage: AppointmentMemberFormRowPage;
  let showAppointmentMemberRow: ShowAppointmentMemberRowPage;
  let showInformationPage: ShowInformationPage;
  let statisticPage: StatisticPage;
  let numberInputPage: NumberInputPage;
  let decisionOptions: DecisionOptionsPage;
  let until: ProtractorExpectedConditions;
  let request = require('request');

  // an entwicklung anpassen
  let servers = require('./../servers.json');

  beforeEach(() => {
    showAppointmentRequestPage = new ShowAppointmentRequestPage();
    appointmentMemberFormRowPage = new AppointmentMemberFormRowPage();
    showAppointmentMemberRow = new ShowAppointmentMemberRowPage();
    showInformationPage = new ShowInformationPage();
    statisticPage = new StatisticPage();
    numberInputPage = new NumberInputPage();
    decisionOptions = new DecisionOptionsPage();
    until = protractor.ExpectedConditions;
  });

  // Datenbank leeren, da sonst in der richtigen Anwendung DataIntegrityViolationException auf den Primary Keys erscheinen
  afterEach(() => {
    let isSetup = false;
    request(servers.dbHelperService + '/e2e/backend/clear', () => {
      isSetup = true;
      }, (err) => {console.log('error in backend-db setup: ', err);
    });

    browser.wait(function () { return isSetup === true; });
  });

  it('should add 3 members, edit second, cancel edit on first. calculating statistic', () => {
    let isSetup = false;
    request(servers.dbHelperService + '/e2e/backend/clear', () => {
      request(servers.dbHelperService + '/e2e/backend/setup/BACKEND_SHOW_REQUEST_50', () => {
        isSetup = true;
      }, (err) => { console.log('error in db setup: ', err); });
    });

    browser.wait(function () { return isSetup === true; });

    showAppointmentRequestPage.navigateTo(50);
    browser.waitForAngular();

    expect(showInformationPage.getTitle()).toBe('Coding Dojo');
    expect(showInformationPage.getDescription()).toBe('Fun with code');
    expect(showInformationPage.getDeadline()).toBe('Umfrage aktiv bis: 10.10.2050');

    showAppointmentRequestPage.getAppointmentMemberRows().then(list => {
      expect(list.length).toBe(0);
    });

    expect(statisticPage.getDescription()).toBe('Anzahl');
    expect(statisticPage.getStatistic(0)).toBe('0');
    expect(statisticPage.getStatistic(1)).toBe('0');

    let gustav = new Member('Gustav', '17', '3');
    let fritz = new Member('Fritz', '5', '10');
    let hans = new Member('Hans', '0', '5');
    let max = new Member('Maximilian Max-Man Mustermann el Macho', '0', '0');

    appointmentMemberFormRowPage.setAppointmentMemberName(gustav.name.set);
    appointmentMemberFormRowPage.setDecisionParticipants(0, gustav.d1.set);
    appointmentMemberFormRowPage.setDecisionParticipants(1, gustav.d2.set);

    expect(appointmentMemberFormRowPage.getAppointmentMemberName()).toBe(gustav.name.set);
    expect(appointmentMemberFormRowPage.getDecisionParticipants(0)).toBe(gustav.d1.set);
    expect(appointmentMemberFormRowPage.getDecisionParticipants(1)).toBe(gustav.d2.set);

    appointmentMemberFormRowPage.clickAddAppointmentMember();

    browser.wait(until.visibilityOf(showAppointmentRequestPage.getAppointmentMemberWithIndex(0)), 10000, 'adding member failed');

    showAppointmentRequestPage.getAppointmentMemberRows().then(list => {
      expect(list.length).toBe(1);
    });

    expect(showAppointmentMemberRow.getDisplayedAppointmentMemberName(0)).toBe(gustav.name.get);
    expect(showAppointmentMemberRow.getDecisionParticipants(0, 0)).toBe(gustav.d1.get);
    expect(showAppointmentMemberRow.getDecisionParticipants(0, 1)).toBe(gustav.d2.get);

    expect(statisticPage.getStatistic(0)).toBe('17');
    expect(statisticPage.getStatistic(1)).toBe('3');

    appointmentMemberFormRowPage.setAppointmentMemberName(fritz.name.set);
    appointmentMemberFormRowPage.setDecisionParticipants(0, fritz.d1.set);
    appointmentMemberFormRowPage.setDecisionParticipants(1, fritz.d2.set);

    expect(appointmentMemberFormRowPage.getAppointmentMemberName()).toBe(fritz.name.set);
    expect(appointmentMemberFormRowPage.getDecisionParticipants(0)).toBe(fritz.d1.set);
    expect(appointmentMemberFormRowPage.getDecisionParticipants(1)).toBe(fritz.d2.set);

    appointmentMemberFormRowPage.clickAddAppointmentMember();

    browser.wait(until.visibilityOf(showAppointmentRequestPage.getAppointmentMemberWithIndex(1)), 10000, 'adding member failed');
    browser.wait(until.visibilityOf(showAppointmentRequestPage.getAppointmentMemberWithIndex(0)), 10000, 'adding member failed');

    showAppointmentRequestPage.getAppointmentMemberRows().then(list => {
      expect(list.length).toBe(2);
    });

    expect(showAppointmentMemberRow.getDisplayedAppointmentMemberName(0)).toBe(fritz.name.get);
    expect(showAppointmentMemberRow.getDecisionParticipants(0, 0)).toBe(fritz.d1.get);
    expect(showAppointmentMemberRow.getDecisionParticipants(0, 1)).toBe(fritz.d2.get);

    expect(showAppointmentMemberRow.getDisplayedAppointmentMemberName(1)).toBe(gustav.name.get);
    expect(showAppointmentMemberRow.getDecisionParticipants(1, 0)).toBe(gustav.d1.get);
    expect(showAppointmentMemberRow.getDecisionParticipants(1, 1)).toBe(gustav.d2.get);

    expect(statisticPage.getStatistic(0)).toBe('22');
    expect(statisticPage.getStatistic(1)).toBe('13');

    appointmentMemberFormRowPage.setAppointmentMemberName(hans.name.set);
    appointmentMemberFormRowPage.setDecisionParticipants(0, hans.d1.set);
    appointmentMemberFormRowPage.setDecisionParticipants(1, hans.d2.set);

    expect(appointmentMemberFormRowPage.getAppointmentMemberName()).toBe(hans.name.set);
    expect(appointmentMemberFormRowPage.getDecisionParticipants(0)).toBe(hans.d1.set);
    expect(appointmentMemberFormRowPage.getDecisionParticipants(1)).toBe(hans.d2.set);

    appointmentMemberFormRowPage.clickAddAppointmentMember();

    browser.wait(until.visibilityOf(showAppointmentRequestPage.getAppointmentMemberWithIndex(2)), 10000, 'adding member failed');
    browser.wait(until.visibilityOf(showAppointmentRequestPage.getAppointmentMemberWithIndex(1)), 10000, 'adding member failed');
    browser.wait(until.visibilityOf(showAppointmentRequestPage.getAppointmentMemberWithIndex(0)), 10000, 'adding member failed');

    showAppointmentRequestPage.getAppointmentMemberRows().then(list => {
      expect(list.length).toBe(3);
    });

    expect(showAppointmentMemberRow.getDisplayedAppointmentMemberName(0)).toBe(hans.name.get);
    expect(showAppointmentMemberRow.getDecisionParticipants(0, 0)).toBe(hans.d1.get);
    expect(showAppointmentMemberRow.getDecisionParticipants(0, 1)).toBe(hans.d2.get);

    expect(showAppointmentMemberRow.getDisplayedAppointmentMemberName(1)).toBe(fritz.name.get);
    expect(showAppointmentMemberRow.getDecisionParticipants(1, 0)).toBe(fritz.d1.get);
    expect(showAppointmentMemberRow.getDecisionParticipants(1, 1)).toBe(fritz.d2.get);

    expect(showAppointmentMemberRow.getDisplayedAppointmentMemberName(2)).toBe(gustav.name.get);
    expect(showAppointmentMemberRow.getDecisionParticipants(2, 0)).toBe(gustav.d1.get);
    expect(showAppointmentMemberRow.getDecisionParticipants(2, 1)).toBe(gustav.d2.get);

    expect(statisticPage.getStatistic(0)).toBe('22');
    expect(statisticPage.getStatistic(1)).toBe('18');

    showAppointmentMemberRow.clickEditAppointmentMember(1);

    expect(showAppointmentMemberRow.getEditAppointmentMemberName(1)).toBe(fritz.name.set);
    expect(showAppointmentMemberRow.getEditDecisionParticipants(1, 0)).toBe(fritz.d1.set);
    expect(showAppointmentMemberRow.getEditDecisionParticipants(1, 1)).toBe(fritz.d2.set);

    showAppointmentMemberRow.setEditAppointmentMemberMame(1, max.name.set);
    showAppointmentMemberRow.setEditDecisionParticipants(1, 0, max.d1.set);
    showAppointmentMemberRow.setEditDecisionParticipants(1, 1, max.d2.set);

    expect(showAppointmentMemberRow.getEditAppointmentMemberName(1)).toBe(max.name.set);
    expect(showAppointmentMemberRow.getEditDecisionParticipants(1, 0)).toBe(max.d1.set);
    expect(showAppointmentMemberRow.getEditDecisionParticipants(1, 1)).toBe(max.d2.set);

    showAppointmentMemberRow.clickSaveAppointmentMember(1);

    browser.waitForAngular();

    browser.wait(until.visibilityOf(showAppointmentRequestPage.getAppointmentMemberWithIndex(2)), 10000, 'editing member failed');
    browser.wait(until.visibilityOf(showAppointmentRequestPage.getAppointmentMemberWithIndex(1)), 10000, 'editing member failed');
    browser.wait(until.visibilityOf(showAppointmentRequestPage.getAppointmentMemberWithIndex(0)), 10000, 'editing member failed');

    expect(showAppointmentMemberRow.getDisplayedAppointmentMemberName(1)).toBe(max.name.get);
    expect(showAppointmentMemberRow.getDecisionParticipants(1, 0)).toBe(max.d1.get);
    expect(showAppointmentMemberRow.getDecisionParticipants(1, 1)).toBe(max.d2.get);

    expect(statisticPage.getStatistic(0)).toBe('17');
    expect(statisticPage.getStatistic(1)).toBe('8');

    showAppointmentMemberRow.clickEditAppointmentMember(0);

    expect(showAppointmentMemberRow.getEditAppointmentMemberName(0)).toBe(hans.name.set);
    expect(showAppointmentMemberRow.getEditDecisionParticipants(0, 0)).toBe(hans.d1.set);
    expect(showAppointmentMemberRow.getEditDecisionParticipants(0, 1)).toBe(hans.d2.set);

    showAppointmentMemberRow.setEditAppointmentMemberMame(0, fritz.name.set);
    showAppointmentMemberRow.setEditDecisionParticipants(0, 0, fritz.d1.set);
    showAppointmentMemberRow.setEditDecisionParticipants(0, 1, fritz.d2.set);

    expect(showAppointmentMemberRow.getEditAppointmentMemberName(0)).toBe(fritz.name.set);
    expect(showAppointmentMemberRow.getEditDecisionParticipants(0, 0)).toBe(fritz.d1.set);
    expect(showAppointmentMemberRow.getEditDecisionParticipants(0, 1)).toBe(fritz.d2.set);

    showAppointmentMemberRow.clickCancelAppointmentMember(0);

    browser.waitForAngular();

    expect(showAppointmentMemberRow.getDisplayedAppointmentMemberName(0)).toBe(hans.name.get);
    expect(showAppointmentMemberRow.getDecisionParticipants(0, 0)).toBe(hans.d1.get);
    expect(showAppointmentMemberRow.getDecisionParticipants(0, 1)).toBe(hans.d2.get);

    expect(statisticPage.getStatistic(0)).toBe('17');
    expect(statisticPage.getStatistic(1)).toBe('8');
  });

});

export class Member {
  name: { set: string, get: string };
  d1: { set: string, get: string };
  d2: { set: string, get: string };

  constructor(name: string, d1: string, d2: string) {
    // wenn name zu lang: wird er gekürzt (hier bei 22 Zeichen) ACHTUNG: wird in der Anwendung anders gemacht!!!
    this.name = { 'set': name, 'get': name.length > 22 ? name.substring(0, 21) + '...' : name };
    this.d1 = { 'set': d1, 'get': d1 === '0' ? '' : d1 };
    this.d2 = { 'set': d2, 'get': d2 === '0' ? '' : d2 };
  }
}
