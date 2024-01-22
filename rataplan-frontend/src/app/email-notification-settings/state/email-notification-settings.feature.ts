import { createFeature } from '@ngrx/store';
import { emailNotificationSettingsReducer } from './email-notification-settings.reducer';

export const emailNotificationSettingsFeature = createFeature({
  name: 'EmailNotificationSettings',
  reducer: emailNotificationSettingsReducer,
});