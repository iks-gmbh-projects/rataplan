import { Component, Injectable, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { combineLatest, startWith, Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { VoteOptionConfig } from '../../../models/vote-option.model';
import { ExtraValidators } from '../../../validator/validators';
import { voteFormAction } from '../state/vote-form.action';
import { voteFormFeature } from '../state/vote-form.feature';

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
    isDateChecked: new UntypedFormControl(false),
    isTimeChecked: new UntypedFormControl(false),
    isEndDateChecked: new UntypedFormControl(false),
    isEndTimeChecked: new UntypedFormControl(false),
    isDescriptionChecked: new UntypedFormControl(false),
    isUrlChecked: new UntypedFormControl(false),
  };
  configForm = new UntypedFormGroup(this.fields);
  private storeSub?: Subscription;
  private formSub1?: Subscription;
  private formSub2?: Subscription;

  constructor(
    private formBuilder: UntypedFormBuilder,
    private router: Router,
    private activeRoute: ActivatedRoute,
    private store: Store,
    private ref:MatDialogRef<any>
  ) {
    this.configForm.setValidators(ExtraValidators.filterCountMin(1));
  }

  ngOnInit(): void {
    this.storeSub = this.store.select(voteFormFeature.selectVote)
      .pipe(
        filter(vote => !!vote),
        map(vote => vote!.voteOptionConfig)
      ).subscribe(voteConfig => {
        this.configForm.setValue({
          isDateChecked: !!voteConfig?.startDate,
          isTimeChecked: !!voteConfig?.startTime,
          isEndDateChecked: !!voteConfig?.endDate,
          isEndTimeChecked: !!voteConfig?.endTime,
          isDescriptionChecked: !!voteConfig?.description,
          isUrlChecked: !!voteConfig?.url,
        });
      });
    this.formSub1 = this.fields.isDateChecked.valueChanges.subscribe((enabled: boolean) => {
      if (enabled) {
        this.fields.isTimeChecked.enable();
        this.fields.isEndDateChecked.enable();
      } else {
        this.fields.isTimeChecked.disable({ emitEvent: false });
        this.fields.isTimeChecked.setValue(false);
        this.fields.isEndDateChecked.disable({ emitEvent: false });
        this.fields.isEndDateChecked.setValue(false);
      }
    });
    this.formSub2 = combineLatest([
      this.fields.isTimeChecked.valueChanges.pipe(startWith(this.fields.isTimeChecked.value)),
      this.fields.isEndDateChecked.valueChanges.pipe(startWith(this.fields.isEndDateChecked.value))
    ]).subscribe(([timeEnabled, endDateEnabled]: [boolean, boolean]) => {
      if (timeEnabled || endDateEnabled) {
        this.fields.isEndTimeChecked.enable();
      } else {
        this.fields.isEndTimeChecked.disable({ emitEvent: false });
        this.fields.isEndTimeChecked.setValue(false);
      }
    });
  }

  ngOnDestroy(): void {
    this.storeSub?.unsubscribe();
    this.formSub1?.unsubscribe();
  }
  
  close(){this.ref.close();}

  save() {
    const config: VoteOptionConfig = {
      startDate: this.configForm.get('isDateChecked')?.value || false,
      startTime: this.configForm.get('isTimeChecked')?.value || false,
      endDate: this.configForm.get('isEndDateChecked')?.value || false,
      endTime: this.configForm.get('isEndTimeChecked')?.value || false,
      description: this.configForm.get('isDescriptionChecked')?.value || false,
      url: this.configForm.get('isUrlChecked')?.value || false,
    };
    this.store.dispatch(voteFormAction.setOptionConfig({config}));
    this.ref.close();
  }
}