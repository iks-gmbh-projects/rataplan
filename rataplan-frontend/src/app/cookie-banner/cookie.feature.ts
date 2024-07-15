import { createFeature } from '@ngrx/store';
import { cookieReducer } from './cookie.reducer';

export const cookieFeature = createFeature({
  name: 'cookie',
  reducer: cookieReducer,
});