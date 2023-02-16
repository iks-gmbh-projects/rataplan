import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject, Subscription } from 'rxjs';

import {FormControl, Validators} from "@angular/forms";
import {NgxMaterialTimepickerTheme} from "ngx-material-timepicker";
import {Router} from "@angular/router";
import { AppointmentModel } from "../../../models/appointment.model";
import { appState } from "../../../app.reducers";
import { Store } from "@ngrx/store";
import { filter, map } from "rxjs/operators";
import { SetAppointmentsAction } from "../../appointment.actions";

@Component({
  selector: 'app-date-overview-subform',
  templateUrl: './date-overview-subform.component.html',
  styleUrls: ['./date-overview-subform.component.css'],
})
export class DateOverviewSubformComponent implements OnInit, OnDestroy {
  destroySubject: Subject<boolean> = new Subject<boolean>();
  title: string = '';
  description?: string;
  appointments: AppointmentModel[] = [];
  showDescription = false;
  isValid = true;

  private storeSub?: Subscription;

  constructor(
    private store: Store<appState>,
    private router: Router
  ) {}
  'time' = new FormControl(null, Validators.required);

  ngOnInit(): void {
    this.storeSub = this.store.select("appointmentRequest")
      .pipe(
        filter(state => !!state.appointmentRequest),
        map(state => state.appointmentRequest!)
      ).subscribe(appointmentRequest => {
        this.title = appointmentRequest.title;
        this.description = appointmentRequest.description;
        this.appointments = appointmentRequest.appointments;
        this.appointments
          .sort((b, a) => new Date(b.startDate!).getTime() - new Date(a.startDate!).getTime());
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
    this.store.dispatch(new SetAppointmentsAction(this.appointments));
  }
  backPage(){
    this.router.navigateByUrl("configurationOptions")
  }

  nextPage() {
    this.store.dispatch(new SetAppointmentsAction(this.appointments));
    this.router.navigateByUrl("email");
  }


}
