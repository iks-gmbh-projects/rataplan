import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-appointment-request-not-found',
  templateUrl: './appointment-request-not-found.component.html',
  styleUrls: ['./appointment-request-not-found.component.css']
})
export class AppointmentRequestNotFoundComponent implements OnInit {

  public requestId: number;

  constructor(private _route: ActivatedRoute) {
    _route.params
      .subscribe(
        params => this.requestId = params['id']
      );
  }

  ngOnInit() {
  }

}
