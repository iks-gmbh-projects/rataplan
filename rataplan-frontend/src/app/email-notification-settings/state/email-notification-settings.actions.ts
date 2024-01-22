import { createActionGroup, emptyProps, props } from '@ngrx/store';
import { EmailCycle, EmailNotificationSettings } from './email-notification-settings.model';

export const emailNotificationSettingsActions = createActionGroup({
  source: 'Email Notification Settings',
  events: {
    fetch: emptyProps(),
    success: props<{response: EmailNotificationSettings}>(),
    failure: props<{error: any}>(),
    'Set Category Setting': props<{notificationCategory: string, cycle: EmailCycle}>(),
    'Clear Category Setting': props<{notificationCategory: string}>(),
    'Set Type Setting': props<{notificationType: string, cycle: EmailCycle}>(),
    'Clear Type Setting': props<{notificationType: string}>(),
    'Save Settings': emptyProps(),
  },
});