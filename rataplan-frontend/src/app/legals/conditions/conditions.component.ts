import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-conditions',
  templateUrl: './conditions.component.html',
  styleUrls: ['./conditions.component.css']
})
export class ConditionsComponent implements OnInit {

  public conditionsUrl = "";
  public privacyUrl = "";
  public contactUrl = "";

  // constructor(private environmentService: EnvironmentService) { }
  //
  // ngOnInit() {
  //   let baseUrl = this.environmentService.baseUrl;
  //   this.conditionsUrl = baseUrl + '/terms-and-conditions';
  //   this.privacyUrl = baseUrl + '/privacy';
  //   this.contactUrl = baseUrl + '/contact';
  // }

  constructor() {
  }

  ngOnInit() {
    let baseUrl = "https://www.drumdibum.de"
    this.conditionsUrl = baseUrl + '/terms-and-conditions';
    this.privacyUrl = baseUrl + '/privacy';
    this.contactUrl = baseUrl + '/contact';
  }
}
