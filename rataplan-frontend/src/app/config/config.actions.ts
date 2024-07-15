import { createActionGroup, emptyProps, props } from '@ngrx/store';
import { Config } from './config.reducer';

export const configActions = createActionGroup({
  source: 'config',
  events: {
    fetch: emptyProps(),
    'fetch success': props<{config: Config}>(),
    error: (error: any) => ({error}),
  }
})