import { Component, EventEmitter, Output } from '@angular/core';
import { AbstractControl, NgForm } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { defined } from '../../../operators/non-empty';
import { FormErrorMessageService } from '../../../services/form-error-message-service/form-error-message.service';
import { Answer, Checkbox, Question, QuestionGroup } from '../../survey.model';
import { surveyFormActions } from '../state/survey-form.action';
import { surveyFormFeature } from '../state/survey-form.feature';

@Component({
  selector: 'app-survey-form-page',
  templateUrl: './page.component.html',
  styleUrls: ['./page.component.css'],
  exportAs: 'appSurveyFormPage'
})
export class PageComponent {
  public readonly questionGroup$: Observable<QuestionGroup>;
  public readonly answers$: Observable<Record<string | number, Answer | undefined>>;
  public readonly preview$: Observable<boolean>;
  public readonly first$: Observable<boolean>;
  @Output() public readonly formAfterViewInit = new EventEmitter<AbstractControl>();

  constructor(
    private readonly store: Store,
    public readonly errorMessageService: FormErrorMessageService,
  ) {
    this.questionGroup$ = store.select(surveyFormFeature.selectCurrentQuestionGroup).pipe(
      defined,
    );
    this.answers$ = store.select(surveyFormFeature.selectCurrentAnswers)
    this.preview$ = store.select(surveyFormFeature.selectPreview);
    this.first$ = store.select(surveyFormFeature.selectPage).pipe(
      map(page => page == 0),
    );
  }

  public submit(form: NgForm) {
    if (form.valid) {
      let answers: {[key: string|number]: Answer&{checkboxId?: string|number}} = form.value;
      for(let key in answers) {
        if(answers[key].checkboxId !== undefined && answers[key].checkboxId !== null) {
          answers[key].checkboxes = {
            ...answers[key].checkboxes,
            [answers[key].checkboxId!]: true
          };
          delete answers[key].checkboxId;
        }
      }
      this.store.dispatch(surveyFormActions.nextPage({answers}))
    }
  }

  public hasTextField(checkboxes: Checkbox[]): boolean {
    return checkboxes.some(checkbox => checkbox.hasTextField);
  }

  public disableTextField(question: Question, answerControl?: AbstractControl): boolean {
    let answer: Answer&{checkboxId?: string|number} = answerControl?.value;
    if(!answer) return false;
    if(answer.checkboxId !== undefined && answer.checkboxId !== null) {
      answer.checkboxes = {
        ...answer.checkboxes,
        [answer.checkboxId!]: true
      };
      delete answer.checkboxId;
    }
    if(!answer.checkboxes) return false;
    for(let checkbox of question.choices!) {
      if(checkbox.hasTextField && answer.checkboxes![checkbox.id!]) return false;
    }
    return true;
  }

  public revert(form: NgForm) {
    let answers: {[key: string|number]: Answer&{checkboxId?: string|number}} = form.value;
    for(let key in answers) {
      if(answers[key].checkboxId !== undefined && answers[key].checkboxId !== null) {
        answers[key].checkboxes = {
          ...answers[key].checkboxes,
          [answers[key].checkboxId!]: true
        };
        delete answers[key].checkboxId;
      }
    }
    this.store.dispatch(surveyFormActions.nextPage({answers}))
  }
  
  protected readonly Object = Object;
}