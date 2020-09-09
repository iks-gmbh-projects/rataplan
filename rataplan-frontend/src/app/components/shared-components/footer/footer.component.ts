import { Component, OnInit } from '@angular/core';
import * as packageJSON from './../../../../../package.json';

@Component({
  selector: 'rp-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent implements OnInit {

  public version;

  constructor() {
    this.version = (<any>packageJSON).version;
  }

  ngOnInit() {
  }

}
