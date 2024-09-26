import { createActionGroup, emptyProps, props } from '@ngrx/store';
import { VoteOptionConfig, VoteOptionModel } from '../../../models/vote-option.model';
import { VoteModel, VoteNotificationSettings } from '../../../models/vote.model';
import { DecisionType } from '../decision-type.enum';

export const voteFormAction = createActionGroup({
  source: 'Vote Form',
  events: {
    'Init': props<{id?: string | number}>(),
    'Init Success': props<{vote: VoteModel}>(),
    'Init Error': props<{error: any}>(),
    'Set General Values': props<{
      title: string,
      description?: string,
      deadline: Date,
      timezone: string,
      timezoneActive: boolean,
      decisionType: DecisionType,
      yesLimitActive: boolean,
      yesAnswerLimit: number | null
    }>(),
    'Set Option Config': props<{config: VoteOptionConfig}>(),
    'Set Options': props<{options: VoteOptionModel[]}>(),
    'Add Options': props<{options: VoteOptionModel[]}>(),
    'Edit Option': props<{index: number, option: VoteOptionModel}>(),
    'Remove Option': props<{index: number}>(),
    'Set Organizer Info': props<{
        name?: string,
        notificationSettings?: VoteNotificationSettings,
        consigneeList: string[],
        userConsignees: (string|number)[],
        personalisedInvitation?:string
      }>(),
    'Preview': emptyProps(),
    'Post': emptyProps(),
    'Post Success': props<{created: VoteModel, editToken?: string}>(),
    'Post Error': props<{error: any}>(),
  },
});

/**
 * Signifies if the given Action requires an VoteModel in store.
 * This is used to unify the code to handle the potential error of no VoteModel being in the store.
 */
export const ActionRequiresInit = {
  [voteFormAction.init.type]: false,
  [voteFormAction.initSuccess.type]: false,
  [voteFormAction.initError.type]: false,
  [voteFormAction.setGeneralValues.type]: true,
  [voteFormAction.setOptionConfig.type]: true,
  [voteFormAction.setOptions.type]: true,
  [voteFormAction.addOptions.type]: true,
  [voteFormAction.editOption.type]: true,
  [voteFormAction.removeOption.type]: true,
  [voteFormAction.setOrganizerInfo.type]: true,
  [voteFormAction.preview.type]: true,
  [voteFormAction.post.type]: true,
  [voteFormAction.postSuccess.type]: false,
  [voteFormAction.postError.type]: false,
} as const;

/**
 * Asserts that ActionsRequiresInit is defined for every VoteOptionAction
 * without affecting how typescript can infer literal types for the values of ActionRequiresInit.
 * E.g. typescript will still know that ActionRequiresInit[VoteOptionActions.INIT] is false at compile time.
 */
const ActionRequiresInitTypeAssertion: {
  readonly [type in typeof voteFormAction[keyof typeof voteFormAction]['type']]: boolean
} = ActionRequiresInit;