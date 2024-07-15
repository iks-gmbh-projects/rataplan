import { createActionGroup, emptyProps, props } from '@ngrx/store';

export const cookieActions = createActionGroup({
  source: 'cookie',
  events: {
    load: emptyProps(),
    accept: props<{onLoad: boolean}>(),
  }
});