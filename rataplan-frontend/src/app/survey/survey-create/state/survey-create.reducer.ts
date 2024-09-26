import { createReducer, on } from '@ngrx/store';
import { QuestionGroup, SurveyHead } from '../../survey.model';
import { DeepPartial, surveyCreateActions } from './survey-create.action';

export const surveyCreateReducer = createReducer<{
  editing: boolean,
  originalStartDate: Date | undefined,
  head: DeepPartial<SurveyHead> | undefined,
  groups: DeepPartial<QuestionGroup[]>,
  currentGroupIndex: number,
  valid: {
    head: boolean,
    groups: boolean[],
  } | undefined,
  showPreview: boolean,
  busy: boolean,
  error: any,
}>(
  {
    editing: false,
    originalStartDate: undefined,
    head: undefined,
    groups: [undefined],
    currentGroupIndex: -1,
    valid: undefined,
    showPreview: false,
    busy: false,
    error: undefined,
  },
  on(
    surveyCreateActions.newSurvey,
    () => (
      {
        editing: false,
        originalStartDate: undefined,
        head: undefined,
        groups: [],
        currentGroupIndex: -1,
        valid: undefined,
        showPreview: false,
        busy: false,
        error: undefined,
      }
    ),
  ),
  on(surveyCreateActions.editSurvey, () => ({
    editing: true,
    originalStartDate: undefined,
    head: undefined,
    groups: [],
    currentGroupIndex: -1,
    valid: undefined,
    showPreview: false,
    busy: true,
    error: undefined,
  })),
  on(surveyCreateActions.editSurveyLoaded, (state, {survey}) => ({
    editing: true,
    originalStartDate: survey.startDate,
    head: {...survey, timezoneActive:!!survey.timezone},
    groups: survey.questionGroups,
    currentGroupIndex: -1,
    valid: undefined,
    showPreview: false,
    busy: false,
    error: undefined,
  })),
  on(surveyCreateActions.editSurveyFailed, (state, {error}) => ({
    ...state,
    busy: false,
    error,
  })),
  on(
    surveyCreateActions.setHead,
    (state, {head}) => (
      {
        ...state,
        head,
        valid: undefined,
      }
    ),
  ),
  on(surveyCreateActions.setGroup, (state, {replacement}) => ({
    ...state,
    groups: state.currentGroupIndex >= state.groups.length ? [...state.groups, replacement] :
      state.currentGroupIndex < 0 ? [replacement, ...state.groups] : state.groups.map((g, i) => i === state.currentGroupIndex ? replacement : g),
    currentGroupIndex: state.currentGroupIndex >= state.groups.length ? state.groups.length :
      state.currentGroupIndex < 0 ? 0 : state.currentGroupIndex,
    valid: undefined,
  })),
  on(surveyCreateActions.previousGroup, (state) => {
    const nidx = Math.max(-1, state.currentGroupIndex - 1);
    return {
      ...state,
      currentGroupIndex: nidx,
    };
  }),
  on(surveyCreateActions.nextGroup, (state) => {
    const nidx = Math.max(0, Math.min(state.groups.length-1, state.currentGroupIndex + 1));
    return {
      ...state,
      currentGroupIndex: nidx,
    };
  }),
  on(surveyCreateActions.removeGroup, (state) => {
    const nidx = Math.max(-1, state.currentGroupIndex-1);
    return {
    ...state,
      groups: state.groups.filter((_, i) => i !== state.currentGroupIndex),
      currentGroupIndex: nidx,
      valid: undefined,
    };
  }),
  on(surveyCreateActions.insertGroup, (state) => {
    const nidx = Math.min(state.groups.length, state.currentGroupIndex + 1);
    const groups = state.groups.flatMap((g, i) => i === nidx ? [undefined, g] : [g]);
    return {
      ...state,
      currentGroupIndex: nidx,
      valid: undefined,
      groups,
    };
  }),
  on(surveyCreateActions.setValidity, (state, {headValid, groupsValid}) => ({
    ...state,
    valid: {
      head: headValid,
      groups: groupsValid,
    },
  })),
  on(surveyCreateActions.preview, (state) => ({
    ...state,
    showPreview: true,
  })),
  on(surveyCreateActions.endPreview, (state) => ({
    ...state,
    showPreview: false,
  })),
  on(surveyCreateActions.postSurvey, (state) => ({
    ...state,
    busy: true,
  })),
  on(surveyCreateActions.postSurveySuccess, (state) => ({
    ...state,
    busy: false,
  })),
  on(surveyCreateActions.postSurveyError, (state, {error}) => ({
    ...state,
    busy: false,
    error,
  })),
);