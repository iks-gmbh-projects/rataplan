import { HomePage } from '../page-objects/home/home.po';
import { browser } from 'protractor';

describe('show home', function () {
  let homePage: HomePage;

  beforeEach(() => {
    homePage = new HomePage();
  });

  it('should display welcome message. navigate via button', () => {
    homePage.navigateTo();
    expect(homePage.getParagraphText()).toEqual('Willkommen bei drumdibum');
    homePage.getStartNowButton().click();

    expect(browser.getCurrentUrl()).toContain('/appointmentrequest');
  });

});
