import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';
import { Store } from '@ngrx/store';
import { combineLatest, filter, first, of } from 'rxjs';
import { catchError, distinctUntilChanged, map, switchMap, tap } from 'rxjs/operators';
import { configFeature } from '../../../config/config.feature';
import { defined } from '../../../operators/non-empty';
import { surveyFormActions } from '../../survey-form/state/survey-form.action';
import { Checkbox, Question, QuestionGroup, Survey, SurveyHead } from '../../survey.model';
import { surveyCreateActions } from './survey-create.action';
import { surveyCreateFeature } from './survey-create.feature';

function ensureDate<T extends SurveyHead>(head: T): T {
  head.startDate = new Date(head.startDate);
  head.endDate = new Date(head.endDate);
  return head;
}

function validateQuestionGroup(group?: QuestionGroup): boolean {
  if(!group) return false;
  return !!group.title && group.title.length < 256 && group.questions.length > 0 &&
    group.questions.every(validateQuestion);
}

function validateQuestion(question: Question): boolean {
  if(!question.text || question.text.length >= 256) return false;
  switch(question.type) {
  case 'OPEN':
    return true;
  case 'CHOICE':
    return question.minSelect !== undefined &&
      question.minSelect >= 0 &&
      question.maxSelect !== undefined &&
      question.maxSelect >= question.minSelect &&
      question.choices !== undefined &&
      question.maxSelect <= question.choices.length &&
      question.choices.every(validateChoices);
  default:
    return false;
  }
}

function validateChoices(choice: Checkbox): boolean {
  return !!choice.text && choice.text.length < 256;
}

@Injectable({providedIn: 'root'})
export class SurveyCreateEffects {
  constructor(
    private readonly store: Store,
    private readonly actions$: Actions,
    private readonly http: HttpClient,
    private readonly router: Router,
  )
  {}
  
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
      return a.head === b.head &&
        a.groups === b.groups &&
        a.valid.head === b.valid.head &&
        (
          a.valid.groups !== undefined && b.valid.groups !== undefined ?
            a.valid.groups.length === b.valid.groups.length &&
            a.valid.groups.every((v, i) => v === b.valid.groups![i]) :
            a.valid.groups === b.valid.groups
        );
    }),
    map(state => surveyCreateActions.setValidity({
      headValid: true,
      groupsValid: state.groups.map(validateQuestionGroup),
    })),
  ));
  
  previewSurvey = createEffect(() => this.actions$.pipe(
    ofType(surveyCreateActions.preview),
    switchMap(() => this.store.select(surveyCreateFeature.selectSurveyCreationState).pipe(first())),
    map(({head, groups}): Survey => (
      {
        ...head!,
        questionGroups: groups.map((g, i) => (
          {
            ...g!,
            id: i,
            questions: g!.questions.map((q, j) => {
              switch(q.type) {
              case 'OPEN':
                return {
                  ...q,
                  rank: j,
                };
              case 'CHOICE':
                return {
                  ...q,
                  rank: j,
                  choices: q.choices.map((c, k) => (
                    {
                      ...c,
                      id: k,
                    }
                  )),
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
        first(),
        filter(({head}) => head !== undefined),
        map(({editing, head, groups}): {editing: boolean, survey: Survey} => (
          {
            editing,
            survey: {
              ...head!,
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