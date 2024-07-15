import { createFeature, createSelector, Selector } from '@ngrx/store';
import { configReducer } from './config.reducer';

function createSubPathSelector<T>(selector: Selector<T, string | undefined>, ...path: (string | number)[]) {
  const p = path.join('/').replace(/\/\/+/g, '/');
  return createSelector(selector, url => url === undefined ? undefined : `${url}${p}`);
}

export const configFeature = createFeature({
  name: 'config',
  reducer: configReducer,
  extraSelectors: ({
    selectAuthBackend,
    selectVoteBackend,
    selectSurveyBackend,
  }) => (
    {
      selectLoginUrl: createSubPathSelector(selectAuthBackend, 'login'),
      selectLogoutUrl: createSubPathSelector(selectAuthBackend, 'logout'),
      selectAuthBackendUrl: (...path: (string | number)[]) => createSubPathSelector(selectAuthBackend, 'v1', ...path),
      selectVoteBackendUrl: (...path: (string | number)[]) => createSubPathSelector(selectVoteBackend, ...path),
      selectSurveyBackendUrl: (...path: (string | number)[]) => createSubPathSelector(selectSurveyBackend, ...path),
    }
  ),
});