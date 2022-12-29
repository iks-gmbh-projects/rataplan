import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';
import {NgxMatTimepickerModule} from "@angular-material-components/datetime-picker";

import { AppointmentRequestFormService } from '../appointment-request-form.service';
import {FormControl, Validators} from "@angular/forms";
import {NgxMaterialTimepickerTheme} from "ngx-material-timepicker";
import {ConfigSubformComponent} from "../config-subform/config-subform.component";
import {Router} from "@angular/router";

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
  selectedTimes;
  showDescription = false;
  isValid = true;

  constructor(private appointmentRequestFormService: AppointmentRequestFormService,
              private router: Router) {
    this.title = appointmentRequestFormService.appointmentRequest.title;
    this.description = appointmentRequestFormService.appointmentRequest.description;
    this.selectedDates = appointmentRequestFormService.selectedDates;
    this.selectedTimes =appointmentRequestFormService.selectedTimes;

  }
  'time' = new FormControl(null, Validators.required);

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

  removeDate(date: Date) {
    this.selectedDates.find((element, index) => {
      if (element == date) this.selectedDates.splice(index, 1);
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
