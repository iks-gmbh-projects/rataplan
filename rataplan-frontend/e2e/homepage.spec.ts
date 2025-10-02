import { test, expect } from '@playwright/test';

test.beforeEach(async({page}) => {
  await page.goto('localhost:4200');
});

test('has title', async({page}) => {
  await expect(page).toHaveTitle('drumdibum');
});

test('change url on "neue Abstimmung"', async({page}) => {
  await page.getByRole('link', {name: 'Neue Abstimmung'}).click();
  await expect(page).toHaveURL(/vote\/create\/general/);
});

test.describe('sidenav', () => {
  test.beforeEach(async({page}) => {
    await page.getByRole('button', {name: 'Toggle sidenav'}).click();
  });
  
  test('change url on  burgerbutton "Abstimmung"', async({page}) => {
    await page.getByRole('link', {name: 'Abstimmung', exact: true}).click();
    await expect(page).toHaveURL(/vote\/create\/general/);
  });
  
  test('change url on  burgerbutton "Umfragen"', async({page}) => {
    await page.getByRole('link', {name: 'Umfragen'}).click();
    await expect(page).toHaveURL(/survey\/list/);
  });
  
  test('change url on  burgerbutton "Feedback"', async({page}) => {
    await page.getByRole('link', {name: 'Feedback'}).click();
    await expect(page).toHaveURL(/feedback/);
  });
});