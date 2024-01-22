import { EmailNotificationSettings } from './email-notification-settings.model';

export type EmailNotificationSettingsState = {
  settings: EmailNotificationSettings | undefined,
  error: any,
  busy: boolean,
};

export const initialState : EmailNotificationSettingsState = {
  settings: undefined,
  error: undefined,
  busy: true,
};