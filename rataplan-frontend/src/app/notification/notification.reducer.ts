import { createReducer, on } from '@ngrx/store';
import { notificationActions } from './notification.actions';

type notificationState = {[type: string]: number};

export const notificationReducer = createReducer<notificationState>(
  {},
  on(notificationActions.notify, (state, action) => ({
    ...state,
    [action.notificationType]: (state[action.notificationType] || 0) + action.count,
  })),
  on(notificationActions.clear, (state, action) => ({
    ...state,
    [action.notificationType]: 0,
  })),
  on(notificationActions.clearall, () => ({}))
);
