import { createReducer, on } from '@ngrx/store';
import { voteNotificationtypes } from '../vote/vote.notificationtypes';
import { notificationActions } from './notification.actions';

type notificationState = {[type: string]: number};

export const notificationReducer = createReducer<notificationState>(
  {},
  on(notificationActions.invitation, (state, action) => {
    let newState = {...state};
    newState[voteNotificationtypes.consigns] ?
      newState[voteNotificationtypes.consigns]++ :
      newState[voteNotificationtypes.consigns] = 1;
    return newState;
  }),
  on(
    notificationActions.notify,
    (state, action) => (
      {
        ...state,
        [action.notificationType]: (
          state[action.notificationType] || 0
        ) + action.count,
      }
    ),
  ),
  on(
    notificationActions.clear,
    (state, action) => (
      {
        ...state,
        [action.notificationType]: 0,
      }
    ),
  ),
  on(
    notificationActions.clearAll,
    () => (
      {}
    ),
  ),
);