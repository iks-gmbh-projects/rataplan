import { Component, OnDestroy, OnInit } from '@angular/core';

import { AppointmentRequestFormService } from '../appointment-request-form.service';

@Component({
  selector: 'app-link-subform',
  templateUrl: './link-subform.component.html',
  styleUrls: ['./link-subform.component.css'],
})
export class LinkSubformComponent implements OnInit, OnDestroy {

  constructor(private appointmentFormService: AppointmentRequestFormService) {
  }

  ngOnInit(): void {
  }

  ngOnDestroy(): void {
    this.appointmentFormService.resetFormObservable.next();
  }

}
