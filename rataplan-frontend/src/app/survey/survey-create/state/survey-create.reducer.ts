import { createReducer, on } from '@ngrx/store';
import { QuestionGroup, SurveyHead } from '../../survey.model';
import { surveyCreateActions } from './survey-create.action';

export const surveyCreateReducer = createReducer<{
  editing: boolean,
  originalStartDate: Date | undefined,
  head: SurveyHead | undefined,
  groups: (QuestionGroup | undefined)[],
  currentGroupIndex: number,
  currentGroup: QuestionGroup | undefined,
  valid: {
    head?: boolean,
    groups?: boolean[],
  },
  showPreview: boolean,
  busy: boolean,
  error: any,
}>(
  {
    editing: false,
    originalStartDate: undefined,
    head: undefined,
    groups: [],
    currentGroupIndex: -1,
    currentGroup: undefined,
    valid: {},
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
        currentGroupIndex: 0,
        currentGroup: undefined,
        valid: {},
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
    currentGroup: undefined,
    valid: {},
    showPreview: false,
    busy: true,
    error: undefined,
  })),
  on(surveyCreateActions.editSurveyLoaded, (state, {survey}) => ({
    editing: true,
    originalStartDate: survey.startDate,
    head: survey,
    groups: survey.questionGroups,
    currentGroupIndex: -1,
    currentGroup: survey.questionGroups[0] ?? {},
    valid: {},
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
        headPage: false,
        valid: {
          ...state.valid,
          head: undefined,
        }
      }
    ),
  ),
  on(surveyCreateActions.previousGroup, (state, {replacement}) => {
    const nidx = Math.max(-1, state.currentGroupIndex - 1);
    const groups = replacement === undefined ? state.groups : state.groups.map((g, i) => i === state.currentGroupIndex ? replacement : g);
    return {
      ...state,
      currentGroupIndex: nidx,
      currentGroup: groups[nidx],
      valid: replacement === undefined ? state.valid : {
        ...state.valid,
        groups: undefined,
      },
      groups,
    };
  }),
  on(surveyCreateActions.nextGroup, (state, {replacement}) => {
    const nidx = Math.min(state.groups.length-1, state.currentGroupIndex + 1);
    const groups = replacement === undefined ? state.groups : state.groups.map((g, i) => i === state.currentGroupIndex ? replacement : g);
    return {
      ...state,
      currentGroupIndex: nidx,
      currentGroup: groups[nidx],
      valid: replacement === undefined ? state.valid : {
        ...state.valid,
        groups: undefined,
      },
      groups,
    };
  }),
  on(surveyCreateActions.removeGroup, (state) => {
    const nidx = Math.max(-1, state.currentGroupIndex-1);
    return {
    ...state,
      groups: state.groups.filter((_, i) => i !== state.currentGroupIndex),
      currentGroupIndex: nidx,
      currentGroup: state.groups[nidx],
      valid: {
        ...state.valid,
        groups: undefined,
      },
    };
  }),
  on(surveyCreateActions.insertGroup, (state, {replacement}) => {
    const nidx = Math.min(state.groups.length, state.currentGroupIndex + 1);
    const groups = state.groups ? state.groups.flatMap((g, i) => i === state.currentGroupIndex ? [replacement ?? g, undefined] : [g]) : [undefined];
    return {
      ...state,
      currentGroupIndex: nidx,
      currentGroup: undefined,
      valid: replacement === undefined ? state.valid : {
        ...state.valid,
        groups: undefined,
      },
      groups,
    };
  }),
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