import { Component, Injectable, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ExtraValidators } from '../../../validator/validators';
import { appState } from "../../../app.reducers";
import { Store } from "@ngrx/store";
import { Subscription } from "rxjs";
import { filter, map } from "rxjs/operators";
import { SetAppointmentConfigAction } from "../../appointment.actions";
import { AppointmentConfig } from "../../../models/appointment.model";

@Component({
  selector: 'app-config-subform',
  templateUrl: './config-subform.component.html',
  styleUrls: ['./config-subform.component.css']
})
@Injectable({
  providedIn: 'root'
})
export class ConfigSubformComponent implements OnInit, OnDestroy {
  configForm = this.formBuilder.group({
    isDateChecked: [false],
    isTimeChecked: [false],
    isEndDateChecked: [false],
    isEndTimeChecked: [false],
    isDescriptionChecked: [false],
    isUrlChecked: [false],
  });
  private storeSub?: Subscription;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private activeRoute: ActivatedRoute,
    private store: Store<appState>
  ) {
    this.configForm.setValidators(ExtraValidators.filterCountMin(1));
  }

  ngOnInit(): void {
    this.storeSub = this.store.select("appointmentRequest")
      .pipe(
        filter(state => !!state.appointmentRequest),
        map(state => state.appointmentRequest!.appointmentRequestConfig.appointmentConfig)
      ).subscribe(appointmentConfig => {
        this.configForm.setValue({
          isDateChecked: appointmentConfig.startDate,
          isTimeChecked: appointmentConfig.startTime,
          isEndDateChecked: appointmentConfig.endDate,
          isEndTimeChecked: appointmentConfig.endTime,
          isDescriptionChecked: appointmentConfig.description,
          isUrlChecked: appointmentConfig.url,
        });
      });
  }

  ngOnDestroy(): void {
    this.storeSub?.unsubscribe();
  }

  nextPage() {
    const config: AppointmentConfig = {
      startDate: this.configForm.get('isDateChecked')?.value || false,
      startTime: this.configForm.get('isTimeChecked')?.value || false,
      endDate: this.configForm.get('isEndDateChecked')?.value || false,
      endTime: this.configForm.get('isEndTimeChecked')?.value || false,
      description: this.configForm.get('isDescriptionChecked')?.value || false,
      url: this.configForm.get('isUrlChecked')?.value || false,
    };
    this.store.dispatch(new SetAppointmentConfigAction(config));
    if(
      config.startDate &&
      !config.startTime &&
      !config.endDate &&
      !config.endTime &&
      !config.description &&
      !config.url
    ) {
      this.router.navigate(['..', 'datepicker'], { relativeTo: this.activeRoute });
    } else {
      this.router.navigate(['..', 'configuration'], { relativeTo: this.activeRoute });
    }
  }
}
