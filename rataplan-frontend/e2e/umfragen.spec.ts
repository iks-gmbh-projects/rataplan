import { expect, mergeTests } from '@playwright/test';
import { navigationTest } from './fixtures/navigation/navigation.fixture';
import { umfragenTest } from './fixtures/umfragen/umfragen.fixture';

const test = mergeTests(navigationTest, umfragenTest);

test.beforeEach(async({navigationToUmfragen, page}) => {
  await navigationToUmfragen.umfrageErstellen();
  await expect(page).toHaveURL(/survey\/create/);
});

test('UmfragenFixture Test Startseite', async({umfragen, page}) => {
  await umfragen.fillFirstPage();
  await umfragen.clickNextPage();
  await expect(page.getByRole('textbox', {name: 'Frageblocküberschrift'})).toBeVisible();
});

test('UmfragenFixture Test zweite Seite', async({umfragen, page}) => {
  await umfragen.fillFirstPage({umfragename: 'Name', beschreibung: 'Beschreibung'});
  await umfragen.clickNextPage();
  await umfragen.fillNextPage();
  await umfragen.fillNextQuestion({frage: 'TEST', erforderlich: true});
  await umfragen.clickVorschau();
  await expect(page.getByText('TEST', {exact: true})).toBeVisible();
});

test('UmfragenFixture Test dritte Seite', async({umfragen, page}) => {
  await umfragen.fillFirstPage();
  await umfragen.clickNextPage();
  await umfragen.fillNextPage();
  await umfragen.fillNextPage({fragenblockueberschrift: 'DREI', frage: 'Test Frage 2'});
  await umfragen.clickVorschau();
  await expect(page.getByText('Test Frage', {exact: true})).toBeVisible();
  await page.getByRole('button').filter({hasText: 'navigate_next'}).click();
  await expect(page.getByText('Test Frage 2', {exact: true})).toBeVisible();
});