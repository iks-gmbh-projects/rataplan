import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {AppointmentRequest} from "../../../../models/appointment-requests/appointment-request";
import {Router} from "@angular/router";

@Component({
  selector: 'rp-own-appointment-request-list',
  templateUrl: './user-owner-list.component.html',
  styleUrls: ['./user-owner-list.component.css']
})
export class UserOwnerListComponent implements OnInit {

  @Input() appointmentRequests: AppointmentRequest[];

  constructor(private router: Router) {

  }

  ngOnInit() {

  }

  onClickRow(id: number) {
    this.router.navigate(['/appointmentrequest/', id]);
  }

  onEdit(id: number) {
    this.router.navigate(['/appointmentrequest/' + id + '/edit']);
  }

}
