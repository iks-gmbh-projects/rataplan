import { Component, OnInit } from '@angular/core';
import { EnvironmentService } from '../../../services/environment-service/environment.service';

@Component({
  selector: 'rp-privacy',
  templateUrl: './privacy.component.html',
  styleUrls: ['./privacy.component.css']
})
export class PrivacyComponent implements OnInit {

  public url;

  constructor(private _environmentService: EnvironmentService) { }

  ngOnInit() {
    this.url = this._environmentService.baseUrl;
  }

}
