import { createActionGroup, emptyProps, props } from '@ngrx/store';
import { EmailCycle, NetworkEmailNotificationSettings } from './email-notification-settings.model';

export const emailNotificationSettingsActions = createActionGroup({
  source: 'Email Notification Settings',
  events: {
    fetch: emptyProps(),
    success: props<{response: NetworkEmailNotificationSettings}>(),
    failure: props<{error: any}>(),
    'Set Default Setting': props<{cycle: EmailCycle}>(),
    'Set Category Setting': props<{notificationCategory: string, cycle: EmailCycle}>(),
    'Clear Category Setting': props<{notificationCategory: string}>(),
    'Set Type Setting': props<{notificationType: string, cycle: EmailCycle}>(),
    'Clear Type Setting': props<{notificationType: string}>(),
    'Save Settings': emptyProps(),
  },
});