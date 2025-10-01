import { test, expect } from '@playwright/test';

test('has title', async({page}) => {
  await page.goto('localhost:4200');
  
  await expect(page).toHaveTitle(/drumdibum/);
});
