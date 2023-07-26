import { ActionReducer, createFeature } from '@ngrx/store';
import { AuthData, authReducer } from './auth.reducer';

export const authFeature = createFeature({
  name: 'auth',
  reducer: authReducer as ActionReducer<AuthData>,
});
