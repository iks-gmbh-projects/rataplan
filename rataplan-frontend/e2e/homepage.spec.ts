import { test, expect } from '@playwright/test';

test.beforeEach(async({page}) => {
  await page.goto('localhost:4200');
});

test('has title', async({page}) => {
  await expect(page).toHaveTitle(/drumdibum/);
});

test('change url on "neue Abstimmung"', async({page}) => {
  await page.getByRole('link', {name: 'Neue Abstimmung'}).click();
  await expect(page).toHaveURL(/vote\/create\/general/);
});
