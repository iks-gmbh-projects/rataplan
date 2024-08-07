import { isConfiguredEqual, matchConfiguration, matchesConfiguration, VoteOptionModel } from '../../../models/vote-option.model';
import { VoteModel } from '../../../models/vote.model';
import { ActionRequiresInit, VoteFormAction, VoteOptionAction } from './vote-form.action';

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

export function voteFormReducer(
  state: voteState = {
    vote: undefined,
    complete: false,
    appointmentsChanged: undefined,
    busy: false,
    error: undefined,
  },
  action: VoteOptionAction,
): voteState {
  if(ActionRequiresInit[action.type] && !state.vote) {
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
  if('voteOptions' in action && !matchConfiguration(action.voteOptions as VoteOptionModel<false>[], state.vote!.voteConfig.voteOptionConfig)) {
    return {
      ...state,
      error: {
        ...state.error,
        invalid_appointment: 'VoteOption information does not match configuration',
      },
    };
  }
  switch(action.type) {
  case VoteFormAction.INIT:
    return {
      vote: undefined,
      complete: false,
      appointmentsChanged: undefined,
      busy: true,
      error: undefined,
    };
  case VoteFormAction.INIT_SUCCESS:
    return {
      vote: action.request,
      complete: isComplete(action.request),
      appointmentsChanged: false,
      busy: false,
      error: undefined,
    };
  case VoteFormAction.INIT_ERROR:
    return {
      vote: undefined,
      complete: false,
      appointmentsChanged: undefined,
      busy: false,
      error: action.error,
    };
  case VoteFormAction.SET_GENERAL_VALUES:
    return assembleRequestState(
      {
        ...state.vote!,
        title: action.payload.title,
        description: action.payload.description,
        deadline: action.payload.deadline.toISOString(),
        voteConfig: {
          ...state.vote!.voteConfig,
          decisionType: action.payload.decisionType,
          yesLimitActive: action.payload.yesLimitActive,
          yesAnswerLimit: action.payload.yesAnswerLimit,
        },
      },
      state.appointmentsChanged!,
    );
  case VoteFormAction.SET_VOTE_CONFIG:
    if(isConfiguredEqual(
      state.vote!.voteConfig.voteOptionConfig,
      action.config,
    ))
    {
      return state;
    }
    return {
      vote: {
        ...state.vote!,
        voteConfig: {
          ...state.vote!.voteConfig,
          voteOptionConfig: {...action.config},
        },
        options: [],
      },
      complete: false,
      appointmentsChanged: true,
      busy: false,
      error: undefined,
    };
  case VoteFormAction.SET_VOTES:
    return assembleRequestState(
      {
        ...state.vote!,
        options: [...action.votes],
      },
      true,
    );
  case VoteFormAction.ADD_VOTES:
    return assembleRequestState(
      {
        ...state.vote!,
        options: [...state.vote!.options, ...action.votes],
      },
      true,
    );
  case VoteFormAction.EDIT_VOTE:
    if(!matchesConfiguration(
      action.voteOption,
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
          ...state.vote!.options.slice(0, action.index),
          action.voteOption,
          ...state.vote!.options.slice(action.index + 1),
        ],
      },
      true,
    );
  case VoteFormAction.REMOVE_VOTE:
    return assembleRequestState(
      {
        ...state.vote!,
        options: [
          ...state.vote!.options.slice(0, action.index),
          ...state.vote!.options.slice(action.index + 1),
        ],
      },
      true,
    );
  case VoteFormAction.SET_ORGANIZER_INFO:
    return {
      vote: {
        ...state.vote!,
        organizerName: action.payload.name,
        notificationSettings: action.payload.notificationSettings,
        consigneeList: action.payload.consigneeList,
        userConsignees: action.payload.userConsignees,
        personalisedInvitation: action.payload.personalisedInvitation,
      },
      complete: state.complete!,
      appointmentsChanged: state.appointmentsChanged!,
      busy: false,
      error: undefined,
    };
  case VoteFormAction.POST:
    return {
      ...state,
      busy: true,
    };
  case VoteFormAction.POST_SUCCESS:
    return {
      vote: undefined,
      complete: false,
      appointmentsChanged: undefined,
      busy: false,
      error: undefined,
    };
  case VoteFormAction.POST_ERROR:
    return {
      ...state,
      busy: false,
      error: action.error,
    };
  }
  return state;
}