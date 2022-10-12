import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';

import { AppointmentRequestFormService } from '../appointment-request-form.service';

@Component({
  selector: 'app-link-subform',
  templateUrl: './link-subform.component.html',
  styleUrls: ['./link-subform.component.css'],
})
export class LinkSubformComponent implements OnInit, OnDestroy {
  destroySubject: Subject<boolean> = new Subject<boolean>();
  link = '/vote/';

  constructor(private appointmentFormService: AppointmentRequestFormService) {
  }

  ngOnInit(): void {
    this.appointmentFormService.createAppointmentRequest()
      .pipe(takeUntil(this.destroySubject))
      .subscribe(
        data => {
          this.link = this.link + data.participationToken;
          // this.appointmentRequest = new AppointmentRequestModel();
          console.log(data);
        });
  }

  ngOnDestroy(): void {
    this.appointmentFormService.resetFormObservable.next();
    this.destroySubject.next(true);
    this.destroySubject.complete();
  }

}
