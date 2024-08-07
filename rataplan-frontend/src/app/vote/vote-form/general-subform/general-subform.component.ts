import { Component, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup, ValidatorFn, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { FormErrorMessageService } from '../../../services/form-error-message-service/form-error-message.service';
import { ExtraValidators } from '../../../validator/validators';
import { Store } from '@ngrx/store';
import { DecisionType } from '../decision-type.enum';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SetGeneralValuesVoteOptionAction } from '../state/vote.actions';
import { voteFeature } from '../state/vote.feature';

@Component({
  selector: 'app-general-subform',
  templateUrl: './general-subform.component.html',
  styleUrls: ['./general-subform.component.css'],
})
export class GeneralSubformComponent implements OnInit, OnDestroy {
  readonly DecisionType = DecisionType;
  minDate: Date;
  maxDate: Date;
  minYesLimit!: number;
  
  generalSubform = new UntypedFormGroup({
    'title': new UntypedFormControl(null, [
      Validators.required,
      Validators.maxLength(100),
      ExtraValidators.containsSomeWhitespace,
    ]),
    'description': new UntypedFormControl(null, [
      Validators.maxLength(1100),
    ]),
    'deadline': new UntypedFormControl(null, Validators.required),
    'decision': new UntypedFormControl(0, Validators.required),
    'yesLimitActive': new UntypedFormControl(false, Validators.required),
    'yesAnswerLimit': new UntypedFormControl(
      null,
      [ExtraValidators.yesAnswerLimitMoreThanZeroOrNull(), Validators.min(this.minYesLimit)],
    ),
  });
  
  showDescription = false;
  
  private storeSub?: Subscription;
  
  constructor(
    private snackBar: MatSnackBar,
    private store: Store,
    public readonly errorMessageService: FormErrorMessageService,
    private router: Router,
    private activeRoute: ActivatedRoute,
  )
  {
    const currentYear = new Date().getFullYear();
    this.minDate = new Date();
    this.minDate.setHours(0, 0, 0, 0);
    this.maxDate = new Date(currentYear + 2, 11, 31);
  }
  
  ngOnInit(): void {
    this.storeSub = this.store.select(voteFeature.selectVote)
      .subscribe({
        next: vote => {
          const title = vote?.title;
          const deadline = vote?.deadline;
          const decision = vote?.voteConfig?.decisionType;
          const yesLimitActive = vote?.voteConfig.yesLimitActive;
          const yesAnswerLimit = vote?.voteConfig.yesAnswerLimit;
          if(title || deadline) {
            this.generalSubform.get('title')?.setValue(title);
            this.generalSubform.get('description')?.setValue(vote.description);
            this.generalSubform.get('deadline')?.setValue(deadline);
            this.generalSubform.get('decision')?.setValue(decision);
            this.generalSubform.get('yesAnswerLimit')?.setValue(yesAnswerLimit);
            this.generalSubform.get('yesLimitActive')?.setValue(yesLimitActive);
          }
          if(this.generalSubform.get('description')?.value) {
            this.showDescription = true;
          }
          
          this.minYesLimit = vote!.voteConfig!.yesAnswerLimit! || 0;
        },
        error: err => {
          this.snackBar.open('Unbekannter Fehler beim Laden der Abstimmungsdaten', 'OK');
        },
      });
  }
  
  ngOnDestroy(): void {
    this.storeSub?.unsubscribe();
  }
  
  addAndDeleteDescription() {
    this.showDescription = !this.showDescription;
  }
  
  sanitiseYesAnswerLimit() {
    this.generalSubform.get('yesAnswerLimit')?.setValue(null);
    this.generalSubform.get('yesAnswerLimit')?.markAsPristine();
    this.generalSubform.get('yesAnswerLimit')?.setErrors(null);
  }
  
  resetYesLimitActiveAndYesNumberLimit() {
    this.generalSubform.get('yesLimitActive')?.setValue(false);
    this.sanitiseYesAnswerLimit();
  }
  
  nextPage() {
    if((
        this.generalSubform.get('yesLimitActive') && Number(this.generalSubform.get('yesAnswerLimit')?.value) <= 0
      )
      || this.generalSubform.get('decision')?.value === 2)
    {
      this.resetYesLimitActiveAndYesNumberLimit();
    }
    this.store.dispatch(new SetGeneralValuesVoteOptionAction({
      title: this.generalSubform.value.title,
      description: this.showDescription ? this.generalSubform.value.description : null,
      deadline: new Date(this.generalSubform.value.deadline),
      decisionType: this.generalSubform.value.decision,
      yesLimitActive: this.generalSubform.value.yesLimitActive,
      yesAnswerLimit: this.generalSubform.value.yesAnswerLimit,
    }));
    this.router.navigate(['..', 'configurationOptions'], {relativeTo: this.activeRoute});
  }
  
  static yesAnswerLimitMoreThanZeroOrNull(currentLimit: number | null): ValidatorFn {
    return (c) => {
      if(currentLimit === null) return null;
      if(c.parent?.get('yesLimitActive')?.value) return c.value >= currentLimit ?
        null :
        {'invalid yes answer limit': true};
      else return null;
    };
  }
}