import {Component, Input, OnInit} from '@angular/core';
import {AppointmentRequest} from "../../../../models/appointment-requests/appointment-request";
import {Router} from "@angular/router";

@Component({
  selector: 'rp-participate-appointment-request-list',
  templateUrl: './user-participant-list.component.html',
  styleUrls: ['./user-participant-list.component.css']
})
export class UserParticipantComponent implements OnInit {

  @Input() appointmentRequests: AppointmentRequest[];

  constructor(private router: Router) {

  }

  ngOnInit() {
  }


  onClickRow(id: number) {
    this.router.navigate(['/appointmentrequest/', id]);
  }

}
