import { test as base } from 'playwright-bdd';
import { NavigationToAbstimmung } from './navigationToAbstimmung';
import { NavigationToFeedback } from './navigationToFeedback';
import { NavigationToUmfragen } from './navigationToUmfragen';

type NavigationFixture = {
  navigationToAbstimmung: NavigationToAbstimmung;
  navigationToFeedback: NavigationToFeedback;
  navigationToUmfragen: NavigationToUmfragen;
}

export const navigationTest = base.extend<NavigationFixture>({
  navigationToAbstimmung: async({page}, use) => {
    await page.goto('localhost:4200');
    await page.getByRole('button', {name: 'Toggle sidenav'}).click();
    await page.getByRole('link', {name: 'Abstimmung', exact: true}).click();
    await use(new NavigationToAbstimmung(page));
  },
  navigationToFeedback: async({page}, use) => {
    await page.goto('localhost:4200');
    await page.getByRole('button', {name: 'Toggle sidenav'}).click();
    await page.getByRole('link', {name: 'Feedback', exact: true}).click();
    await use(new NavigationToFeedback(page));
  },
  navigationToUmfragen: async({page}, use) => {
    await page.goto('localhost:4200');
    await page.getByRole('button', {name: 'Toggle sidenav'}).click();
    await page.getByRole('link', {name: 'Umfragen', exact: true}).click();
    await use(new NavigationToUmfragen(page));
  },
});