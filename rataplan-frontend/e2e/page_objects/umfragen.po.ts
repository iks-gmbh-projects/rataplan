import { expect, Page } from '@playwright/test';

export class UmfragenPo {
  constructor(private page: Page) {}
  
  public async checkSecondPage() {
    await expect(this.page.getByRole('textbox', {name: 'Frageblocküberschrift'})).toBeVisible();
  }
  
  public async checkQuestionVisible(frage: string) {
    await expect(this.page.getByText(frage, {exact: true})).toBeVisible();
  }
}