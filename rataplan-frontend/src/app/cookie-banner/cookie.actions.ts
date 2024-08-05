import { createActionGroup, emptyProps } from '@ngrx/store';

export const cookieActions = createActionGroup({
  source: 'cookie',
  events: {
    load: emptyProps(),
    accept: emptyProps(),
    reject: emptyProps(),
  }
});