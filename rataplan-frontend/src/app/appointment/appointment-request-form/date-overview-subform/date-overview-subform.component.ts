import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';

import { AppointmentRequestFormService } from '../../appointment-request-form.service';

@Component({
  selector: 'app-date-overview-subform',
  templateUrl: './date-overview-subform.component.html',
  styleUrls: ['./date-overview-subform.component.css'],
})
export class DateOverviewSubformComponent implements OnInit, OnDestroy {
  destroySubject: Subject<boolean> = new Subject<boolean>();
  title;
  description;
  selectedDates;

  constructor(private appointmentRequestFormService: AppointmentRequestFormService) {
    this.title = appointmentRequestFormService.appointmentRequest.title;
    this.description = appointmentRequestFormService.appointmentRequest.description;
    this.selectedDates = appointmentRequestFormService.selectedDates;
  }

  ngOnInit(): void {
    this.selectedDates
      .sort((b, a) => new Date(b).getTime() - new Date(a).getTime());
    Promise
      .resolve()
      .then(() => this.appointmentRequestFormService.updateLength());

    this.appointmentRequestFormService.submitButtonObservable
      .pipe(takeUntil(this.destroySubject))
      .subscribe(() => {
        this.appointmentRequestFormService.setSelectedDates(this.selectedDates);
      });
  }

  ngOnDestroy(): void {
    this.destroySubject.next(true);
    this.destroySubject.complete();
  }

  removeDate(date: Date) {
    this.selectedDates.find((element, index) => {
      if (element == date) this.selectedDates.splice(index, 1);
    });
    this.appointmentRequestFormService.updateLength();
  }
}
