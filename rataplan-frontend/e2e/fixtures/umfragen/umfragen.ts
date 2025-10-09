import { Page } from '@playwright/test';

export class Umfragen {
  constructor(public readonly page: Page) {
  }
  
  async fillFirstPage() {
    await this.page.getByRole('textbox', {name: 'Umfragename'}).fill('Testname');
    await this.page.getByRole('textbox', {name: 'Beschreibung'}).fill('Testbeschreibung');
    await this.page.locator('mat-form-field').filter({hasText: 'Bis'}).getByLabel('Open calendar').click();
    await this.page.getByRole('button', {name: 'Next month'}).click();
    await this.page.getByText('21', {exact: true}).click();
    await this.page.getByText('23').click();
    await this.page.getByText('30').click();
    await this.page.getByRole('button').filter({hasText: 'navigate_next'}).click();
  }
  
  async fillNextPage() {
    await this.page.getByRole('textbox', {name: 'Frageblocküberschrift'}).fill('Testfrageblocküberschrift');
    await this.fillNextQuestion();
  }
  
  async fillNextQuestion() {
    if(await this.page.getByRole('textbox', {name: 'Frage'}).last().inputValue() !== '') {
      await this.page.locator('div').filter({hasText: /^add$/}).getByRole('button').click();
    }
    await this.page.getByRole('textbox', {name: 'Frage'}).last().fill('Test Frage');
    await this.page.getByRole('textbox', {name: 'Frage'}).last().blur();
    await this.page.getByRole('checkbox', {name: 'Erforderlich'}).last().click();
  }
  
  async clickVorschau() {
    await this.page.getByRole('button', {name: 'Vorschau'}).click();
  }
}
