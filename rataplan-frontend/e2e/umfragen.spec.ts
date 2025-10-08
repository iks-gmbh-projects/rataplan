import { navigationTest } from './fixtures/navigation/navigation.fixture';
import { expect } from '@playwright/test';

navigationTest('umfragen', async({navigationToUmfragen, page}) => {
  await navigationToUmfragen.umfrageErstellen();
  await expect(page).toHaveURL(/survey\/create/);
});