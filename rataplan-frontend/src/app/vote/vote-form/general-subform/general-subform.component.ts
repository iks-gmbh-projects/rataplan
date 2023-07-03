import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { FormErrorMessageService } from '../../../services/form-error-message-service/form-error-message.service';
import { ExtraValidators } from '../../../validator/validators';
import { appState } from '../../../app.reducers';
import { Store } from '@ngrx/store';
import { SetGeneralValuesVoteOptionAction } from '../../vote.actions';
import { DecisionType } from '../decision-type.enum';

@Component({
  selector: 'app-general-subform',
  templateUrl: './general-subform.component.html',
  styleUrls: ['./general-subform.component.css'],
})
export class GeneralSubformComponent implements OnInit, OnDestroy {
  readonly DecisionType = DecisionType;
  minDate: Date;
  maxDate: Date;

  generalSubform = new FormGroup({
    'title': new FormControl(null, [Validators.required, ExtraValidators.containsSomeWhitespace]),
    'description': new FormControl(null),
    'deadline': new FormControl(null, Validators.required),
    'decision': new FormControl(0, Validators.required),
    'yesLimitActive': new FormControl(false, Validators.required),
    'yesAnswerLimit': new FormControl(null, ExtraValidators.yesAnswerLimitMoreThanZeroOrNull())
  });

  showDescription = false;

  private storeSub?: Subscription;

  constructor(
    private store: Store<appState>,
    public readonly errorMessageService: FormErrorMessageService,
    private router: Router,
    private activeRoute: ActivatedRoute
  ) {
    const currentYear = new Date().getFullYear();
    this.minDate = new Date();
    this.minDate.setHours(0, 0, 0, 0);
    this.maxDate = new Date(currentYear + 2, 11, 31);
  }

  ngOnInit(): void {
    this.storeSub = this.store.select('vote')
      .subscribe(state => {
        const vote = state.vote;
        const title = vote?.title;
        const deadline = vote?.deadline;
        const decision = vote?.voteConfig?.decisionType;
        const yesLimitActive = vote?.voteConfig.yesLimitActive;
        const yesAnswerLimit = vote?.voteConfig.yesAnswerLimit;
        if (title || deadline) {
          this.generalSubform.get('title')?.setValue(title);
          this.generalSubform.get('description')?.setValue(vote.description);
          this.generalSubform.get('deadline')?.setValue(deadline);
          this.generalSubform.get('decision')?.setValue(decision);
          this.generalSubform.get('yesAnswerLimit')?.setValue(yesAnswerLimit);
          this.generalSubform.get('yesLimitActive')?.setValue(yesLimitActive);
        }
        if (this.generalSubform.get('description')?.value) {
          this.showDescription = true;
        }
      });
  }

  ngOnDestroy(): void {
    this.storeSub?.unsubscribe();
  }

  addAndDeleteDescription() {
    this.showDescription = !this.showDescription;
    if (!this.showDescription) {
      this.generalSubform.get('description')?.setValue(null);
    }
  }

  sanitiseYesAnswerLimit() {
    this.generalSubform.get('yesAnswerLimit')?.setValue(null);
    this.generalSubform.get('yesAnswerLimit')?.markAsPristine();
    this.generalSubform.get('yesAnswerLimit')?.setErrors(null);
  }

  resetYesLimitActiveAndYesNumberLimit(){
    this.generalSubform.get('yesLimitActive')?.setValue(false);
    this.sanitiseYesAnswerLimit();
  }

  nextPage() {
    if ((this.generalSubform.get('yesLimitActive') && Number(this.generalSubform.get('yesAnswerLimit')?.value) <= 0)
      || this.generalSubform.get('decision')?.value === 2) {
      this.resetYesLimitActiveAndYesNumberLimit();
    }
    this.store.dispatch(new SetGeneralValuesVoteOptionAction({
      title: this.generalSubform.value.title,
      description: this.generalSubform.value.description,
      deadline: new Date(this.generalSubform.value.deadline),
      decisionType: this.generalSubform.value.decision,
      yesLimitActive: this.generalSubform.value.yesLimitActive,
      yesAnswerLimit: this.generalSubform.value.yesAnswerLimit
    }));
    console.log(this.generalSubform.get('title'));
    console.log(this.generalSubform.get('deadline'));
    console.log(this.generalSubform.get('yesAnswerLimit'));
    console.log(this.generalSubform.get('yesLimitActive'));
    this.router.navigate(['..', 'configurationOptions'], { relativeTo: this.activeRoute });
  }
}
