import { createFeature } from '@ngrx/store';
import { notificationReducer } from './notification.reducer';

export const notificationFeature = createFeature({
  name: "Notification",
  reducer: notificationReducer,
});
