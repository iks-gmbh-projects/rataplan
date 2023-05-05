import { Component, Injectable, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ExtraValidators } from '../../../validator/validators';
import { appState } from "../../../app.reducers";
import { Store } from "@ngrx/store";
import { combineLatest, startWith, Subscription } from "rxjs";
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
  private readonly fields = {
    isDateChecked: new FormControl(false),
    isTimeChecked: new FormControl(false),
    isEndDateChecked: new FormControl(false),
    isEndTimeChecked: new FormControl(false),
    isDescriptionChecked: new FormControl(false),
    isUrlChecked: new FormControl(false),
  };
  configForm = new FormGroup(this.fields);
  private storeSub?: Subscription;
  private formSub1?: Subscription;
  private formSub2?: Subscription;

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
    this.formSub1 = this.fields.isDateChecked.valueChanges.subscribe((enabled: boolean) => {
      if(enabled) {
        this.fields.isTimeChecked.enable();
        this.fields.isEndDateChecked.enable();
      } else {
        this.fields.isTimeChecked.disable({emitEvent: false});
        this.fields.isTimeChecked.setValue(false);
        this.fields.isEndDateChecked.disable({emitEvent: false});
        this.fields.isEndDateChecked.setValue(false);
      }
    });
    this.formSub2 = combineLatest([
      this.fields.isTimeChecked.valueChanges.pipe(startWith(this.fields.isTimeChecked.value)),
      this.fields.isEndDateChecked.valueChanges.pipe(startWith(this.fields.isEndDateChecked.value))
    ]).subscribe(([timeEnabled, endDateEnabled]: [boolean, boolean]) => {
      if(timeEnabled || endDateEnabled) {
        this.fields.isEndTimeChecked.enable();
      } else {
        this.fields.isEndTimeChecked.disable({emitEvent: false});
        this.fields.isEndTimeChecked.setValue(false);
      }
    })
  }

  ngOnDestroy(): void {
    this.storeSub?.unsubscribe();
    this.formSub1?.unsubscribe();
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
