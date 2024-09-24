import { createActionGroup, emptyProps, props } from '@ngrx/store';
import { VoteModel } from '../../../models/vote.model';
import { VoteOptionDecisionType } from '../../vote-form/decision-type.enum';

export const voteAction = createActionGroup({
  source: 'Vote Form',
  events: {
    'Load': props<{id: string | number}>(),
    'Load Success': props<{vote: VoteModel, preview: boolean}>(),
    'Error': props<{error: any}>(),
    'Select Participant': props<{index: number}>(),
    'Delete Participant': props<{index: number}>(),
    'Delete Participant Intermediary': props<{index: number, id: string | number}>(),
    'Delete Participant Success': emptyProps(),
    'Set Name': props<{name: string}>(),
    'Set Decision': props<{
      option: string | number,
    } & ({
      decision: VoteOptionDecisionType,
      participants?: undefined,
    } | {
      decision?: undefined,
      participants: number,
    })>(),
    'Cycle Decision': props<{option: string | number}>(),
    'Reset Participant': emptyProps(),
    'Submit Participant': emptyProps(),
    'Submit Participant Success': emptyProps(),
  },
});