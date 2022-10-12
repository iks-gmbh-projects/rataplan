import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-date-range',
  templateUrl: './date-range.component.html',
  styleUrls: ['./date-range.component.css']
})
export class DateRangeComponent {
  private _startDate?: Date;
  private  _endDate?: Date;
  public get startDate() {
    return this._startDate;
  }
  @Input() public set startDate(value: Date|undefined) {
    if(value) {
      this._startDate = new Date(value);
      this._startDate.setMilliseconds(0);
    } else {
      this._startDate = value;
    }
  }
  public get endDate() {
    return this._endDate;
  }
  @Input() public set endDate(value: Date|undefined) {
    if(value) {
      this._endDate = new Date(value);
      this._endDate.setMilliseconds(0);
    } else {
      this._endDate = value;
    }
  }
}
