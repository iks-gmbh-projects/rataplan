import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';
import { Store } from '@ngrx/store';
import { combineLatest, debounceTime, filter, first, of } from 'rxjs';
import { catchError, distinctUntilChanged, map, switchMap, tap } from 'rxjs/operators';
import { configFeature } from '../../../config/config.feature';
import { defined } from '../../../operators/non-empty';
import { routerSelectors } from '../../../router.selectors';
import { surveyFormActions } from '../../survey-form/state/survey-form.action';
import { Checkbox, ChoiceQuestion, OpenQuestion, OrderChoice, OrderQuestion, Question, QuestionGroup, Survey, SurveyHead } from '../../survey.model';
import { DeepPartial, surveyCreateActions } from './survey-create.action';
import { surveyCreateFeature } from './survey-create.feature';

function ensureDate<T extends SurveyHead>(head: T): T {
  head.startDate = new Date(head.startDate);
  head.endDate = new Date(head.endDate);
  return head;
}

function validateQuestionGroup(group?: DeepPartial<QuestionGroup>): group is QuestionGroup {
  if(!group) return false;
  return !!group.title && !!group.questions && group.title.length < 256 && group.questions.length > 0 &&
    group.questions.every(validateQuestion);
}

function validateQuestion(question?: DeepPartial<Question>): question is Question {
  if(!question || !question.text || question.text.length >= 256) return false;
  switch(question.type) {
  case 'OPEN':
    return true;
  case 'CHOICE':
    return question.minSelect !== undefined &&
      question.minSelect >= 0 &&
      question.maxSelect !== undefined &&
      question.maxSelect >= question.minSelect &&
      question.choices !== undefined &&
      question.choices.length > 0 &&
      question.maxSelect <= question.choices.length &&
      question.choices.every(validateChoiceChoices);
  case 'ORDER':
    return question.choices !== undefined && question.choices.length > 0 && question.choices?.every(validateOrderChoices);
  default:
    return false;
  }
}

function validateChoiceChoices(choice?: DeepPartial<Checkbox>): choice is Checkbox {
  return !!choice && !!choice.text && choice.text.length < 256;
}

function validateOrderChoices(choice?: DeepPartial<OrderChoice>): choice is OrderChoice {
  return !!choice && !!choice.text && choice.text.length < 256;
}

@Injectable()
export class SurveyCreateEffects {
  constructor(
    private readonly store: Store,
    private readonly actions$: Actions,
    private readonly http: HttpClient,
    private readonly router: Router,
  )
  {}
  
  autoLoad = createEffect(() => this.store.select(routerSelectors.selectRouteData).pipe(
    map(d => d?.['loadSurveyEdit'] as boolean),
    filter(d => d),
    switchMap(() => this.store.select(routerSelectors.selectRouteParam('accessID')).pipe(
      debounceTime(1),
      first(),
    )),
    map(accessId => accessId ?
      surveyCreateActions.editSurvey({accessId}) :
      surveyCreateActions.newSurvey()
    )
  ));
  
  loadSurvey = createEffect(() => this.actions$.pipe(
    ofType(surveyCreateActions.editSurvey),
    concatLatestFrom(() => this.store.select(configFeature.selectSurveyBackendUrl('surveys')).pipe(defined)),
    switchMap(([{accessId}, url]) => {
      return this.http.get<Survey>(url, {
        params: new HttpParams().append('accessId', accessId),
        withCredentials: true,
      }).pipe(
        map(ensureDate),
        map(survey => surveyCreateActions.editSurveyLoaded({survey})),
        catchError(error => of(surveyCreateActions.editSurveyFailed({error}))),
      );
    }),
  ));
  
  validateSurvey = createEffect(() => this.store.select(surveyCreateFeature.selectSurveyCreationState).pipe(
    distinctUntilChanged((a, b) => {
      return a.head === b.head && a.groups === b.groups && (b.valid !== undefined);
    }),
    map(state => surveyCreateActions.setValidity({
      headValid: true,
      groupsValid: state.groups.map(validateQuestionGroup),
    })),
  ));
  
  previewSurvey = createEffect(() => this.actions$.pipe(
    ofType(surveyCreateActions.preview),
    switchMap(() => this.store.select(surveyCreateFeature.selectSurveyCreationState).pipe(first(({valid}) => !!valid))),
    filter(({valid}) => valid!.head && valid!.groups.length > 0 && valid!.groups.every(b => b)),
    map(({head, groups}): Survey => (
      {
        ...(head as SurveyHead),
        questionGroups: groups!.map((g, i): QuestionGroup => (
          {
            ...(g as QuestionGroup),
            id: i,
            questions: g!.questions!.map((q, j): Question => {
              switch(q!.type!) {
              case 'OPEN':
                return {
                  ...(q as OpenQuestion),
                  rank: j,
                };
              case 'CHOICE':
                return {
                  ...(q as ChoiceQuestion),
                  rank: j,
                  choices: q!.choices!.map((c, k): Checkbox => (
                    {
                      ...(c as Checkbox),
                      id: k,
                    }
                  )),
                };
              case 'ORDER':
                return {
                  ...(q as OrderQuestion),
                  rank: j,
                  choices: q!.choices!.map((c, k): OrderChoice => (
                    {
                      ...(c as OrderChoice),
                      id: k,
                    }
                  ))
                };
              }
            }),
          }
        )),
      }
    )),
    map(survey => surveyFormActions.initPreview({survey})),
  ));
  
  postSurvey = createEffect(() => this.actions$.pipe(
    ofType(surveyCreateActions.postSurvey),
    switchMap(() => combineLatest({
      state: this.store.select(surveyCreateFeature.selectSurveyCreationState).pipe(
        first(({valid}) => !!valid),
        filter(({valid}) => valid!.head && valid!.groups.length > 0 && valid!.groups.every(b => b)),
        map(({editing, head, groups}): {editing: boolean, survey: Survey} => (
          {
            editing,
            survey: {
              ...(head as SurveyHead),
              questionGroups: groups as QuestionGroup[],
            },
          }
        )),
      ),
      url: this.store.select(configFeature.selectSurveyBackendUrl('surveys')).pipe(
        defined,
        first(),
      ),
    })),
    switchMap(({url, state}) => {
      return (
        state.editing ?
          this.http.put<SurveyHead>(url, state.survey, {
            params: new HttpParams().append('accessId', state.survey.accessId!),
            withCredentials: true,
          }) :
          this.http.post<SurveyHead>(url, state.survey, {
            withCredentials: true,
          })
      ).pipe(
        map(({accessId, participationId}) => surveyCreateActions.postSurveySuccess({
          accessId: accessId!,
          participationId: participationId!,
        })),
        catchError(error => of(surveyCreateActions.postSurveyError({error}))),
      );
    }),
  ));
  
  postSurveySuccess = createEffect(() => this.actions$.pipe(
    ofType(surveyCreateActions.postSurveySuccess),
    tap(({accessId}) => {
      this.router.navigate(['/survey', 'access', accessId]);
    }),
  ), {dispatch: false});
}