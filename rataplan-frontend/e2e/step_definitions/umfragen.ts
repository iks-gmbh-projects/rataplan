import { createBdd } from 'playwright-bdd';
import { test } from '../fixtures/mergedFixtures';
import { UmfragenPo } from '../page_objects/umfragen.po';

const {Given, When, Then} = createBdd(test);

Given(
  /^der Benutzer hat die Umfragen-Seite geöffnet und eine neue Umfrage erstellt$/,
  async function({navigationToUmfragen}) {
    await navigationToUmfragen.umfrageErstellen();
  },
);

When(/^der Benutzer die erste Seite ausfüllt$/, async function({umfragen}) {
  await umfragen.fillFirstPage();
});

When(/^der Benutzer weiter navigiert$/, async function({umfragen}) {
  await umfragen.clickNextPage();
});

Then(/^landet der Benutzer auf der zweiten Seite$/, async function({page}) {
  const umfragenPO = new UmfragenPo(page);
  await umfragenPO.checkSecondPage();
});