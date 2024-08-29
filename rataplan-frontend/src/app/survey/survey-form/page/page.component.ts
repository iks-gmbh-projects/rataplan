import { CdkDragDrop } from '@angular/cdk/drag-drop';
import { Component, EventEmitter, Output } from '@angular/core';
import { AbstractControl, NgForm } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Observable, share } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { defined } from '../../../operators/non-empty';
import { FormErrorMessageService } from '../../../services/form-error-message-service/form-error-message.service';
import { Answer, Checkbox, ChoiceQuestion, OrderChoice, OrderQuestion, QuestionGroup } from '../../survey.model';
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
  public readonly last$: Observable<boolean>;
  @Output() public readonly formAfterViewInit = new EventEmitter<AbstractControl>();

  constructor(
    private readonly store: Store,
    public readonly errorMessageService: FormErrorMessageService,
  ) {
    this.questionGroup$ = store.select(surveyFormFeature.selectCurrentQuestionGroup).pipe(
      defined,
      tap(() => this.indexes = {}),
      share(),
    );
    this.answers$ = store.select(surveyFormFeature.selectCurrentAnswers).pipe(
      tap(a => this.indexes = Object.fromEntries(Object.entries(a).map(([k, v]) => [k, v?.order]).filter(([,v]) => v))),
      share(),
    )
    this.preview$ = store.select(surveyFormFeature.selectPreview);
    this.first$ = store.select(surveyFormFeature.selectPage).pipe(
      map(page => page == 0),
    );
    this.last$ = store.select(surveyFormFeature.selectSurveyFormState).pipe(
      map(({survey, page, preview}) => preview && page >= (survey?.questionGroups?.length ?? 0) - 1)
    );
  }

  public submit(form: NgForm) {
    if (form.valid) {
      let answers: Record<string|number, Answer&{checkboxId?: string|number}> = form.value;
      for(let key in answers) {
        if(answers[key].checkboxId !== undefined && answers[key].checkboxId !== null) {
          answers[key].checkboxes = {
            ...answers[key].checkboxes,
            [answers[key].checkboxId!]: true
          };
          delete answers[key].checkboxId;
        }
      }
      for(const [key, value] of Object.entries(this.indexes)) {
        answers[key] = {
          order: value,
        };
      }
      this.store.dispatch(surveyFormActions.nextPage({answers}))
    }
  }

  public hasTextField(checkboxes: Checkbox[]): boolean {
    return checkboxes.some(checkbox => checkbox.hasTextField);
  }

  public disableTextField(question: ChoiceQuestion, answerControl?: AbstractControl): boolean {
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
    this.store.dispatch(surveyFormActions.previousPage({answers}))
  }
  
  private indexes: Partial<Record<string|number, (string|number)[]>> = {};
  
  protected indexArray(question: OrderQuestion): (string|number)[] {
    this.indexes[question.rank!] ??= question.choices.map(v => v.id!);
    return this.indexes[question.rank!]!;
  }
  
  protected choiceText(arr: OrderChoice[], id: string|number): string {
    return arr.find(o => o.id === id)!.text;
  }
  
  protected reorder(question: OrderQuestion, event: CdkDragDrop<unknown>): void {
    const old = this.indexArray(question);
    if(event.previousIndex <= event.currentIndex) {
      this.indexes[question.rank!] = [
        ...old.slice(0, event.previousIndex),
        ...old.slice(event.previousIndex+1, event.currentIndex),
        old[event.previousIndex],
        ...old.slice(event.currentIndex),
      ];
    } else {
      this.indexes[question.rank!] = [
        ...old.slice(0, event.currentIndex),
        old[event.previousIndex],
        ...old.slice(event.currentIndex, event.previousIndex),
        ...old.slice(event.previousIndex+1),
      ];
    }
  }
  
  protected readonly Object = Object;
}