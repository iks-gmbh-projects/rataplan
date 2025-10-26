import { Page } from '@playwright/test';

export class Umfragen {
  constructor(public readonly page: Page) {
  }
  
  defaults = {
    firstPage: {
      umfragename: 'Testname',
      beschreibung: 'Testbeschreibung',
    },
    nextPage: {
      fragenblockueberschrift: 'Testfrageblocküberschrift',
      frage: 'Test Frage',
      erforderlich: false,
    },
  };
  
  async fillFirstPage(data?: {umfragename?: string, beschreibung?: string}) {
    await this.page.getByRole('textbox', {name: 'Umfragename'}).fill(data?.umfragename ??
      this.defaults.firstPage.umfragename);
    await this.page.getByRole('textbox', {name: 'Beschreibung'}).fill(data?.beschreibung ??
      this.defaults.firstPage.beschreibung);
    await this.page.locator('mat-form-field').filter({hasText: 'Bis'}).getByLabel('Open calendar').click();
    await this.page.getByRole('button', {name: 'Next month'}).click();
    await this.page.getByText('21', {exact: true}).click();
    await this.page.getByText('23').click();
    await this.page.getByText('30').click();
  }
  
  async clickNextPage() {
    await this.page.getByRole('button').filter({hasText: 'navigate_next'}).click();
  }
  
  async fillNextPage(data?: {
    fragenblockueberschrift?: string,
    frage?: string,
    erforderlich?: boolean,
  })
  {
    if(await this.page.getByRole('textbox', {name: 'Frageblocküberschrift'}).inputValue() !== '') {
      await this.page.getByRole('button').filter({hasText: 'add'}).nth(1).click();
    }
    await this.page.getByRole('textbox', {name: 'Frageblocküberschrift'}).fill(data?.fragenblockueberschrift ??
      this.defaults.nextPage.fragenblockueberschrift);
    await this.fillNextQuestion(data);
  }
  
  async fillNextQuestion(data?: {
    frage?: string,
    erforderlich?: boolean,
  })
  {
    if(await this.page.getByRole('textbox', {name: 'Frage'}).last().inputValue() !== '') {
      await this.page.locator('div').filter({hasText: /^add$/}).getByRole('button').click();
    }
    await this.page.getByRole('textbox', {name: 'Frage'}).last().fill(data?.frage ?? this.defaults.nextPage.frage);
    await this.page.getByRole('textbox', {name: 'Frage'}).last().blur();
    if(data?.erforderlich ?? this.defaults.nextPage.erforderlich) {
      await this.page.getByRole('checkbox', {name: 'Erforderlich'}).last().click();
    }
  }
  
  async clickVorschau() {
    await this.page.getByRole('button', {name: 'Vorschau'}).click();
  }
}
