import { createFeature } from '@ngrx/store';
import { contactsReducer } from './contacts.reducer';

export const contactsFeature = createFeature({
  name: 'Contacts',
  reducer: contactsReducer,
});