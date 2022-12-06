import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';

import { AppointmentRequestModel } from '../../../models/appointment-request.model';
import { AppointmentRequestFormService } from '../appointment-request-form.service';

@Component({
  selector: 'app-link-subform',
  templateUrl: './link-subform.component.html',
  styleUrls: ['./link-subform.component.css'],
})
export class LinkSubformComponent implements OnInit, OnDestroy {
  destroySubject: Subject<boolean> = new Subject<boolean>();
  participationLink = '/vote/';
  editLink = '/vote/edit/';

  constructor(private appointmentFormService: AppointmentRequestFormService) {
  }

  ngOnInit(): void {
    this.appointmentFormService.createAppointmentRequest()
      .pipe(takeUntil(this.destroySubject))
      .subscribe(
        data => {
          this.participationLink += data.participationToken;
          this.editLink += data.editToken;
          console.log(data);
          this.appointmentFormService.appointmentRequest = new AppointmentRequestModel();
          this.appointmentFormService.selectedDates = [];
        });
  }

  ngOnDestroy(): void {
    this.appointmentFormService.resetFormObservable.next();
    this.destroySubject.next(true);
    this.destroySubject.complete();
  }

}
