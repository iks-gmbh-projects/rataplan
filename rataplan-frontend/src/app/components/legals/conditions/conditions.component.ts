import { Component, OnInit } from '@angular/core';
import { EnvironmentService } from '../../../services/environment-service/environment.service';

@Component({
  selector: 'rp-conditions',
  templateUrl: './conditions.component.html',
  styleUrls: ['./conditions.component.css']
})
export class ConditionsComponent implements OnInit {

  public conditionsUrl;
  public privacyUrl;
  public contactUrl;

  constructor(private _environmentService: EnvironmentService) { }

  ngOnInit() {
    let baseUrl = this._environmentService.baseUrl;
    this.conditionsUrl = baseUrl + '/terms-and-conditions';
    this.privacyUrl = baseUrl + '/privacy';
    this.contactUrl = baseUrl + '/contact';
  }

}
