import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';

import { AppointmentRequestFormService } from '../appointment-request-form.service';
import {FormControl, Validators} from "@angular/forms";
import {NgxMaterialTimepickerTheme} from "ngx-material-timepicker";
import {Router} from "@angular/router";
import { AppointmentModel } from "../../../models/appointment.model";

@Component({
  selector: 'app-date-overview-subform',
  templateUrl: './date-overview-subform.component.html',
  styleUrls: ['./date-overview-subform.component.css'],
})
export class DateOverviewSubformComponent implements OnInit, OnDestroy {
  destroySubject: Subject<boolean> = new Subject<boolean>();
  title;
  description;
  appointments: AppointmentModel[];
  showDescription = false;
  isValid = true;

  constructor(private appointmentRequestFormService: AppointmentRequestFormService,
              private router: Router) {
    this.title = appointmentRequestFormService.appointmentRequest.title;
    this.description = appointmentRequestFormService.appointmentRequest.description;
    this.appointments = appointmentRequestFormService.appointmentRequest.appointments;
  }
  'time' = new FormControl(null, Validators.required);

  ngOnInit(): void {
    this.appointments
      .sort((b, a) => new Date(b.startDate!).getTime() - new Date(a.startDate!).getTime());
    Promise
      .resolve()
      .then(() => this.appointmentRequestFormService.updateLength());

    this.appointmentRequestFormService.submitButtonObservable
      .pipe(takeUntil(this.destroySubject))
      .subscribe(() => {
        this.appointmentRequestFormService.setAppointments(this.appointments);

      });
  }
  blueTheme: NgxMaterialTimepickerTheme = {
    container: {
      bodyBackgroundColor: '#fff',
      buttonColor: '#3f51b5'
    },
    dial: {
      dialBackgroundColor: '#3f51b5',
    },
    clockFace: {
      clockFaceBackgroundColor: '#eeeeee',
      clockHandColor: '#3f51b5',
      clockFaceTimeInactiveColor: '#1D1818',
      clockFaceInnerTimeInactiveColor: '#1D1818'
    }
  };

  ngOnDestroy(): void {
    this.destroySubject.next(true);
    this.destroySubject.complete();
  }

  removeDate(appointment: AppointmentModel) {
    this.appointments.find((element, index) => {
      if (element == appointment) this.appointments.splice(index, 1);
    });
    this.appointmentRequestFormService.updateLength();
  }
  backPage(){
    this.router.navigateByUrl("configurationOptions")
  }

  nextPage() {
    this.router.navigateByUrl("")
  }


}
