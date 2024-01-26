import { createActionGroup, emptyProps, props } from '@ngrx/store';
import { AllContacts } from '../models/contact.model';

export const contactActions = createActionGroup({
  source: 'Contacts',
  events: {
    'reset': emptyProps(),
    'fetch': emptyProps(),
    'error': props<{error: any}>(),
    'fetch success': props<{contacts: AllContacts}>(),
    'change success': emptyProps(),
    'create contact': props<{userId: string|number}>(),
    'delete contact': props<{userId: string|number}>(),
    'create group': props<{name: string}>(),
    'rename group': props<{id: string|number, name: string}>(),
    'delete group': props<{id: string|number}>(),
    'add to group': props<{groupId: string|number, contactId: string|number}>(),
    'remove from group': props<{groupId: string|number, contactId: string|number}>(),
  },
});