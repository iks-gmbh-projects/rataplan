import { Page } from '@playwright/test';

export class NavigationToUmfragen {
  constructor(public readonly page: Page) {
  }
  
  async umfrageErstellen(): Promise<void> {
    await this.page.getByRole('link', {name: 'Erstellen'}).click();
  }
}