import { by, element } from 'protractor';

export class StatisticPage {
  private tag = '//tr[@rp-statistic]';

  public getDescription() {
    return element(by.xpath(this.tag)).element(by.css('.fix-left-col')).getText();
  }

  public getStatistic(columnIndex: number) {
    return element(by.xpath(this.tag)).element(by.css('td:nth-child(' + (columnIndex + 2) + ')')).getText();
  }

}
