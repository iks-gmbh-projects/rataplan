import { ActionReducer, createFeature } from '@ngrx/store';
import { CookieData, cookieReducer } from './cookie.reducer';

export const cookieFeature = createFeature({
  name: 'cookie',
  reducer: cookieReducer as ActionReducer<CookieData>,
});
