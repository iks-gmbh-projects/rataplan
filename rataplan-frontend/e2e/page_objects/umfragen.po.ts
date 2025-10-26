import { expect, Page } from '@playwright/test';

export class UmfragenPo {
  constructor(private page: Page) {}
  
  public async navigateToUmfragenAndCreateNew() {
    await this.page.goto('localhost:4200');
    await this.page.getByRole('button', {name: 'Toggle sidenav'}).click();
    await this.page.getByRole('link', {name: 'Umfragen', exact: true}).click();
    await this.page.getByRole('link', {name: 'Erstellen'}).click();
  }
  
  public async fillFirstPage() {
    await this.page.getByRole('textbox', {name: 'Umfragename'}).fill('umfragename');
    await this.page.getByRole('textbox', {name: 'Beschreibung'}).fill('beschreibung');
    await this.page.locator('mat-form-field').filter({hasText: 'Bis'}).getByLabel('Open calendar').click();
    await this.page.getByRole('button', {name: 'Next month'}).click();
    await this.page.getByText('21', {exact: true}).click();
    await this.page.getByText('23').click();
    await this.page.getByText('30').click();
  }
  
  public async navigateToNextPage() {
    await this.page.getByRole('button').filter({hasText: 'navigate_next'}).click();
  }
  
  public async checkSecondPage() {
    await expect(this.page.getByRole('textbox', {name: 'Frageblocküberschrift'})).toBeVisible();
  }
}