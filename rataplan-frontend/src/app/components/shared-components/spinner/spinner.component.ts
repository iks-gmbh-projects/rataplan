import { Component, Input, OnInit } from '@angular/core';
import { isNullOrUndefined } from 'util';

@Component({
  selector: 'rp-spinner',
  templateUrl: './spinner.component.html',
  styleUrls: ['./spinner.component.css']
})
export class SpinnerComponent implements OnInit {

  @Input() spinnerStyles;

  constructor() {
  }

  ngOnInit() {
    if (isNullOrUndefined(this.spinnerStyles)) {
      this.usePageLoadSpinner();
    }
  }


  private usePageLoadSpinner() {
    this.spinnerStyles = {

    }
  }

}
