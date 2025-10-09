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
  await expect(page.getByRole('textbox', {name: 'Frageblocküberschrift'})).toBeVisible();
});

test('UmfragenFixture Test zweite Seite', async({umfragen, page}) => {
  await umfragen.fillFirstPage();
  await umfragen.fillNextPage();
  await umfragen.clickVorschau();
  await expect(page.getByText('Test Frage', {exact: true})).toBeVisible();
});