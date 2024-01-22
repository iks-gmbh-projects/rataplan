import { createReducer, on } from '@ngrx/store';
import { emailNotificationSettingsActions } from './email-notification-settings.actions';
import { initialState } from './email-notification-settings.state';

function deleteFromCopy<A extends string|number|symbol, B, R extends Record<A, B>>(r: R, del: A): R {
  const ret = {...r};
  delete ret[del];
  return ret;
}

export const emailNotificationSettingsReducer = createReducer(
  initialState,
  on(emailNotificationSettingsActions.fetch, (state) => ({
    ...state,
    busy: true,
  })),
  on(emailNotificationSettingsActions.saveSettings, (state) => ({
    ...state,
    busy: true,
  })),
  on(emailNotificationSettingsActions.success, (state, {response}) => ({
    ...state,
    settings: response,
    busy: false,
    error: undefined,
  })),
  on(emailNotificationSettingsActions.failure, (state, {error}) => ({
    ...state,
    busy: false,
    error,
  })),
  on(emailNotificationSettingsActions.setCategorySetting, (state, {notificationCategory, cycle}) => ({
    ...state,
    settings: {
      ...state.settings!,
      categorySettings: {
        ...state.settings!.categorySettings,
        [notificationCategory]: cycle,
      },
    },
  })),
  on(emailNotificationSettingsActions.clearCategorySetting, (state, {notificationCategory}) => ({
    ...state,
    settings: {
      ...state.settings!,
      categorySettings: deleteFromCopy(state.settings!.categorySettings, notificationCategory),
    },
  })),
  on(emailNotificationSettingsActions.setTypeSetting, (state, {notificationType, cycle}) => ({
    ...state,
    settings: {
      ...state.settings!,
      typeSettings: {
        ...state.settings!.typeSettings,
        [notificationType]: cycle,
      },
    },
  })),
  on(emailNotificationSettingsActions.clearTypeSetting, (state, {notificationType}) => ({
    ...state,
    settings: {
      ...state.settings!,
      typeSettings: deleteFromCopy(state.settings!.typeSettings, notificationType),
    },
  })),
);