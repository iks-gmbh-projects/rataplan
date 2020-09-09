import { Component, Input } from '@angular/core';
import { EnvironmentService } from "../../../../services/environment-service/environment.service";

@Component({
  selector: 'rp-appointment-request-link',
  templateUrl: './appointment-request-link.component.html',
  styleUrls: ['./appointment-request-link.component.css']
})
export class AppointmentRequestLinkComponent {

  public baseUrl: string;
  @Input() id: number;
  @Input() mail: string;

  constructor(
    private environmentService: EnvironmentService
  ) {
    this.baseUrl = environmentService.baseUrl;
  }
}
