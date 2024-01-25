import { VoteModel } from '../models/vote.model';
import { isConfiguredEqual, matchConfiguration, matchesConfiguration } from '../models/vote-option.model';
import { ActionRequiresInit, VoteActions, VoteOptionAction } from './vote.actions';

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

export function voteReducer(
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
  if('voteOptions' in action && !matchConfiguration(action, state.vote!.voteConfig.voteOptionConfig)) {
    return {
      ...state,
      error: {
        ...state.error,
        invalid_appointment: 'VoteOption information does not match configuration',
      },
    };
  }
  switch(action.type) {
  case VoteActions.INIT:
    return {
      vote: undefined,
      complete: false,
      appointmentsChanged: undefined,
      busy: true,
      error: undefined,
    };
  case VoteActions.INIT_SUCCESS:
    return {
      vote: action.request,
      complete: isComplete(action.request),
      appointmentsChanged: false,
      busy: false,
      error: undefined,
    };
  case VoteActions.INIT_ERROR:
    return {
      vote: undefined,
      complete: false,
      appointmentsChanged: undefined,
      busy: false,
      error: action.error,
    };
  case VoteActions.SET_GENERAL_VALUES:
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
  case VoteActions.SET_VOTE_CONFIG:
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
  case VoteActions.SET_VOTES:
    return assembleRequestState(
      {
        ...state.vote!,
        options: [...action.votes],
      },
      true,
    );
  case VoteActions.ADD_VOTES:
    return assembleRequestState(
      {
        ...state.vote!,
        options: [...state.vote!.options, ...action.votes],
      },
      true,
    );
  case VoteActions.EDIT_VOTE:
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
  case VoteActions.REMOVE_VOTE:
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
  case VoteActions.SET_ORGANIZER_INFO:
    return {
      vote: {
        ...state.vote!,
        organizerName: action.payload.name,
        organizerMail: action.payload.email,
        consigneeList: action.payload.consigneeList,
        personalisedInvitation: action.payload.personalisedInvitation,
      },
      complete: state.complete!,
      appointmentsChanged: state.appointmentsChanged!,
      busy: false,
      error: undefined,
    };
  case VoteActions.POST:
    return {
      ...state,
      busy: true,
    };
  case VoteActions.POST_SUCCESS:
    return {
      vote: undefined,
      complete: false,
      appointmentsChanged: undefined,
      busy: false,
      error: undefined,
    };
  case VoteActions.POST_ERROR:
    return {
      ...state,
      busy: false,
      error: action.error,
    };
  }
  return state;
}
