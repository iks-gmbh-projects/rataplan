import { createBdd } from 'playwright-bdd';
import { test } from '../fixtures/mergedFixtures';
import { UmfragenPo } from '../page_objects/umfragen.po';

const {Given, When, Then} = createBdd(test);

Given(/^der Benutzer hat die Umfragen-Seite geöffnet und eine neue Umfrage erstellt$/, async function({page}) {
  const umfragenPO = new UmfragenPo(page);
  await umfragenPO.navigateToUmfragenAndCreateNew();
});

When(/^der Benutzer die erste Seite ausfüllt$/, async function({page}) {
  const umfragenPO = new UmfragenPo(page);
  await umfragenPO.fillFirstPage();
});

When(/^der Benutzer weiter navigiert$/, async function({page}) {
  const umfragenPO = new UmfragenPo(page);
  await umfragenPO.navigateToNextPage();
});

Then(/^landet der Benutzer auf der zweiten Seite$/, async function({page}) {
  const umfragenPO = new UmfragenPo(page);
  await umfragenPO.checkSecondPage();
});