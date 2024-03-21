import { Action } from '@ngrx/store';

import { VoteModel, VoteNotificationSettings } from '../models/vote.model';
import { VoteOptionConfig, VoteOptionModel } from '../models/vote-option.model';
import { DecisionType } from './vote-form/decision-type.enum';

export const VoteActions = {
  INIT: '[Vote] init',
  INIT_SUCCESS: '[Vote] init success',
  INIT_ERROR: '[Vote] init error',
  SET_GENERAL_VALUES: '[Vote] set general values',
  SET_VOTE_CONFIG: '[Vote] set vote config',
  SET_VOTES: '[Vote] set votes',
  ADD_VOTES: '[Vote] add votes',
  EDIT_VOTE: '[Vote] edit votes',
  REMOVE_VOTE: '[Vote] remove vote',
  SET_ORGANIZER_INFO: '[Vote] set organizer info',
  POST: '[Vote] post',
  POST_SUCCESS: '[Vote] post success',
  POST_ERROR: '[Vote] post error',
} as const;

/**
 * Signifies if the given Action requires an VoteModel in store.
 * This is used to unify the code to handle the potential error of no VoteModel being in the store.
 */
export const ActionRequiresInit = {
  [VoteActions.INIT]: false,
  [VoteActions.INIT_SUCCESS]: false,
  [VoteActions.INIT_ERROR]: false,
  [VoteActions.SET_GENERAL_VALUES]: true,
  [VoteActions.SET_VOTE_CONFIG]: true,
  [VoteActions.SET_VOTES]: true,
  [VoteActions.ADD_VOTES]: true,
  [VoteActions.EDIT_VOTE]: true,
  [VoteActions.REMOVE_VOTE]: true,
  [VoteActions.SET_ORGANIZER_INFO]: true,
  [VoteActions.POST]: true,
  [VoteActions.POST_SUCCESS]: false,
  [VoteActions.POST_ERROR]: false,
} as const;

/**
 * Asserts that ActionsRequiresInit is defined for every VoteOptionAction
 * without affecting how typescript can infer literal types for the values of ActionRequiresInit.
 * E.g. typescript will still know that ActionRequiresInit[VoteOptionActions.INIT] is false at compile time.
 */
const ActionRequiresInitTypeAssertion: {
  readonly [type in typeof VoteActions[keyof typeof VoteActions]]: boolean
} = ActionRequiresInit;

export class InitVoteAction implements Action {
  readonly type = VoteActions.INIT;

  constructor(
    readonly id?: string | number
  ) {
  }
}

export class InitVoteSuccessAction implements Action {
  readonly type = VoteActions.INIT_SUCCESS;

  constructor(
    readonly request: VoteModel
  ) {
  }
}

export class InitVoteErrorAction implements Action {
  readonly type = VoteActions.INIT_ERROR;

  constructor(
    readonly error: any
  ) {
  }
}

export class SetGeneralValuesVoteOptionAction implements Action {
  readonly type = VoteActions.SET_GENERAL_VALUES;

  constructor(
    readonly payload: {
      title: string,
      description?: string,
      deadline: Date,
      decisionType: DecisionType,
      yesLimitActive: boolean,
      yesAnswerLimit: number | null
    }
  ) {
  }
}

export class SetVoteOptionConfigAction implements Action {
  readonly type = VoteActions.SET_VOTE_CONFIG;

  constructor(
    readonly config: VoteOptionConfig
  ) {
  }
}

export class SetVoteOptionsAction implements Action {
  readonly type = VoteActions.SET_VOTES;

  constructor(
    readonly votes: VoteOptionModel[]
  ) {
  }
}

export class AddVoteOptionsAction implements Action {
  readonly type = VoteActions.ADD_VOTES;
  readonly votes: VoteOptionModel[];

  constructor(
    ...votes: VoteOptionModel[]
  ) {
    this.votes = votes;
  }
}

export class EditVoteOptionAction implements Action {
  readonly type = VoteActions.EDIT_VOTE;

  constructor(
    readonly index: number,
    readonly voteOption: VoteOptionModel
  ) {
  }
}

export class RemoveVoteOptionAction implements Action {
  readonly type = VoteActions.REMOVE_VOTE;

  constructor(
    readonly index: number
  ) {
  }
}

export class SetOrganizerInfoVoteOptionAction implements Action {
  readonly type = VoteActions.SET_ORGANIZER_INFO;

  constructor(
    readonly payload: {
      name?: string,
      notificationSettings?: VoteNotificationSettings,
      consigneeList: string[],
      userConsignees: (string|number)[],
      personalisedInvitation?:string
    }
  ) {
  }
}

export class PostVoteAction implements Action {
  readonly type = VoteActions.POST;
}

export class PostVoteSuccessAction implements Action {
  readonly type = VoteActions.POST_SUCCESS;

  constructor(
    readonly created: VoteModel,
    readonly editToken?: string,
  ) {
  }
}

export class PostVoteErrorAction implements Action {
  readonly type = VoteActions.POST_ERROR;

  constructor(
    readonly error: any
  ) {
  }
}

export type VoteOptionAction =
  InitVoteAction
  | InitVoteSuccessAction
  | InitVoteErrorAction
  | SetGeneralValuesVoteOptionAction
  | SetVoteOptionConfigAction
  | SetVoteOptionsAction
  | AddVoteOptionsAction
  | EditVoteOptionAction
  | RemoveVoteOptionAction
  | SetOrganizerInfoVoteOptionAction
  | PostVoteAction
  | PostVoteSuccessAction
  | PostVoteErrorAction;
