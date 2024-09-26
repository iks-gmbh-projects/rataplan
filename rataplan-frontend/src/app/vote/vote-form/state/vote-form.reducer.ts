import { Action, createReducer, on } from '@ngrx/store';
import { isConfiguredEqual, matchConfiguration, matchesConfiguration, VoteOptionModel } from '../../../models/vote-option.model';
import { VoteModel } from '../../../models/vote.model';
import { ActionRequiresInit, voteFormAction } from './vote-form.action';

export type voteState = {
  busy: boolean,
  error: any,
} & ({
  vote: undefined,
  complete: false,
  appointmentsChanged: undefined,
} | {
  vote: VoteModel,
  complete: boolean,
  appointmentsChanged: boolean,
});

function isComplete(vote?: VoteModel): boolean {
  return !!vote &&
    !!vote.title &&
    !!vote.deadline &&
    vote.options.length > 0;
}

function assembleRequestState(request: VoteModel, appointmentsChanged: boolean): voteState {
  return {
    vote: request,
    complete: isComplete(request),
    appointmentsChanged: appointmentsChanged,
    busy: false,
    error: undefined,
  };
}

const voteFormReducerPart = createReducer<voteState>(
  {
    vote: undefined,
    complete: false,
    appointmentsChanged: undefined,
    busy: false,
    error: undefined,
  },
  on(
    voteFormAction.init,
    () => (
      {
        vote: undefined,
        complete: false,
        appointmentsChanged: undefined,
        busy: true,
        error: undefined,
      }
    ),
  ),
  on(
    voteFormAction.initSuccess,
    (state, {vote}) => (
      {
        vote: vote,
        complete: isComplete(vote),
        appointmentsChanged: false,
        busy: false,
        error: undefined,
      }
    ),
  ),
  on(
    voteFormAction.initError,
    (state, {error}) => (
      {
        vote: undefined,
        complete: false,
        appointmentsChanged: undefined,
        busy: false,
        error: error,
      }
    ),
  ),
  on(voteFormAction.setGeneralValues, (state, action) => assembleRequestState(
    {
      ...state.vote!,
      title: action.title,
      description: action.description,
      deadline: action.deadline.toISOString(),
      timezone: action.timezone,
      timezoneActive: action.timezoneActive,
      voteConfig: {
        ...state.vote!.voteConfig,
        decisionType: action.decisionType,
        yesLimitActive: action.yesLimitActive,
        yesAnswerLimit: action.yesAnswerLimit,
      },
    },
    state.appointmentsChanged!,
  )),
  on(voteFormAction.setOptionConfig, (state, {config}) => {
    if(isConfiguredEqual(
      state.vote!.voteConfig.voteOptionConfig,
      config,
    ))
    {
      return state;
    }
    return {
      vote: {
        ...state.vote!,
        voteConfig: {
          ...state.vote!.voteConfig,
          voteOptionConfig: {...config},
        },
        options: [],
      },
      complete: false,
      appointmentsChanged: true,
      busy: false,
      error: undefined,
    };
  }),
  on(voteFormAction.setOptions, (state, {options}) => assembleRequestState(
    {
      ...state.vote!,
      options: [...options],
    },
    true,
  )),
  on(voteFormAction.addOptions, (state, {options}) => assembleRequestState(
    {
      ...state.vote!,
      options: [...state.vote!.options, ...options],
    },
    true,
  )),
  on(voteFormAction.editOption, (state, {index, option}) => {
    if(!matchesConfiguration(
      option,
      state.vote!.voteConfig.voteOptionConfig,
    )) return {
      ...state,
      error: {
        ...state.error,
        invalid_appointment: 'VoteOption information does not match configuration',
      },
    };
    return assembleRequestState(
      {
        ...state.vote!,
        options: [
          ...state.vote!.options.slice(0, index),
          option,
          ...state.vote!.options.slice(index + 1),
        ],
      },
      true,
    );
  }),
  on(voteFormAction.removeOption, (state, {index}) => assembleRequestState(
    {
      ...state.vote!,
      options: state.vote!.options.filter((o, i) => i !== index),
    },
    true,
  )),
  on(
    voteFormAction.setOrganizerInfo,
    (state, {name, notificationSettings, consigneeList, userConsignees, personalisedInvitation}) => (
      {
        vote: {
          ...state.vote!,
          organizerName: name,
          notificationSettings,
          consigneeList,
          userConsignees,
          personalisedInvitation,
        },
        complete: state.complete!,
        appointmentsChanged: state.appointmentsChanged!,
        busy: false,
        error: undefined,
      }
    ),
  ),
  on(
    voteFormAction.post,
    state => (
      {
        ...state,
        busy: true,
      }
    ),
  ),
  on(
    voteFormAction.postSuccess,
    () => (
      {
        vote: undefined,
        complete: false,
        appointmentsChanged: undefined,
        busy: false,
        error: undefined,
      }
    ),
  ),
  on(
    voteFormAction.postError,
    (state, {error}) => (
      {
        ...state,
        busy: false,
        error,
      }
    ),
  ),
);

export function voteFormReducer(
  state: voteState = {
    vote: undefined,
    complete: false,
    appointmentsChanged: undefined,
    busy: false,
    error: undefined,
  },
  action: Action,
): voteState {
  if(ActionRequiresInit[action.type as keyof typeof ActionRequiresInit] && !state.vote) {
    return {
      vote: undefined,
      complete: false,
      appointmentsChanged: undefined,
      busy: false,
      error: {
        ...state.error,
        missing_request: 'Initialize request first',
      },
    };
  }
  if('voteOptions' in action &&
    !matchConfiguration(action.voteOptions as VoteOptionModel[], state.vote!.voteConfig.voteOptionConfig))
  {
    return {
      ...state,
      error: {
        ...state.error,
        invalid_appointment: 'VoteOption information does not match configuration',
      },
    };
  }
  return voteFormReducerPart(state, action);
}