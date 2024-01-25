import { createReducer, on } from '@ngrx/store';
import { AllContacts } from '../models/contact.model';
import { contactActions } from './contacts.actions';

const initialState: AllContacts&{
  busy: boolean,
  error: any,
} = {
  busy: false,
  error: undefined,
  groups: [],
  ungrouped: [],
}

export const contactsReducer = createReducer(initialState,
  on(contactActions.fetch, (state) => ({...state, busy: true})),
  on(contactActions.fetchSuccess, (state, {contacts}) => ({
    ...state,
    ...contacts,
    error: undefined,
    busy: false,
  })),
  on(contactActions.error, (state, {error}) => ({
    ...state,
    busy: false,
    error,
  })),
  on(contactActions.reset, () => initialState)
);