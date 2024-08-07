import { Action } from '@ngrx/store';
import { VoteOptionConfig, VoteOptionModel } from '../../../models/vote-option.model';
import { VoteModel, VoteNotificationSettings } from '../../../models/vote.model';
import { DecisionType } from '../decision-type.enum';

export const VoteFormAction = {
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
  [VoteFormAction.INIT]: false,
  [VoteFormAction.INIT_SUCCESS]: false,
  [VoteFormAction.INIT_ERROR]: false,
  [VoteFormAction.SET_GENERAL_VALUES]: true,
  [VoteFormAction.SET_VOTE_CONFIG]: true,
  [VoteFormAction.SET_VOTES]: true,
  [VoteFormAction.ADD_VOTES]: true,
  [VoteFormAction.EDIT_VOTE]: true,
  [VoteFormAction.REMOVE_VOTE]: true,
  [VoteFormAction.SET_ORGANIZER_INFO]: true,
  [VoteFormAction.POST]: true,
  [VoteFormAction.POST_SUCCESS]: false,
  [VoteFormAction.POST_ERROR]: false,
} as const;

/**
 * Asserts that ActionsRequiresInit is defined for every VoteOptionAction
 * without affecting how typescript can infer literal types for the values of ActionRequiresInit.
 * E.g. typescript will still know that ActionRequiresInit[VoteOptionActions.INIT] is false at compile time.
 */
const ActionRequiresInitTypeAssertion: {
  readonly [type in typeof VoteFormAction[keyof typeof VoteFormAction]]: boolean
} = ActionRequiresInit;

export class InitVoteAction implements Action {
  readonly type = VoteFormAction.INIT;

  constructor(
    readonly id?: string | number
  ) {
  }
}

export class InitVoteSuccessAction implements Action {
  readonly type = VoteFormAction.INIT_SUCCESS;

  constructor(
    readonly request: VoteModel
  ) {
  }
}

export class InitVoteErrorAction implements Action {
  readonly type = VoteFormAction.INIT_ERROR;

  constructor(
    readonly error: any
  ) {
  }
}

export class SetGeneralValuesVoteOptionAction implements Action {
  readonly type = VoteFormAction.SET_GENERAL_VALUES;

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
  readonly type = VoteFormAction.SET_VOTE_CONFIG;

  constructor(
    readonly config: VoteOptionConfig
  ) {
  }
}

export class SetVoteOptionsAction implements Action {
  readonly type = VoteFormAction.SET_VOTES;

  constructor(
    readonly votes: VoteOptionModel[]
  ) {
  }
}

export class AddVoteOptionsAction implements Action {
  readonly type = VoteFormAction.ADD_VOTES;
  readonly votes: VoteOptionModel[];

  constructor(
    ...votes: VoteOptionModel[]
  ) {
    this.votes = votes;
  }
}

export class EditVoteOptionAction implements Action {
  readonly type = VoteFormAction.EDIT_VOTE;

  constructor(
    readonly index: number,
    readonly voteOption: VoteOptionModel
  ) {
  }
}

export class RemoveVoteOptionAction implements Action {
  readonly type = VoteFormAction.REMOVE_VOTE;

  constructor(
    readonly index: number
  ) {
  }
}

export class SetOrganizerInfoVoteOptionAction implements Action {
  readonly type = VoteFormAction.SET_ORGANIZER_INFO;

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
  readonly type = VoteFormAction.POST;
}

export class PostVoteSuccessAction implements Action {
  readonly type = VoteFormAction.POST_SUCCESS;

  constructor(
    readonly created: VoteModel,
    readonly editToken?: string,
  ) {
  }
}

export class PostVoteErrorAction implements Action {
  readonly type = VoteFormAction.POST_ERROR;

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