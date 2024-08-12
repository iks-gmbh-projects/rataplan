import { inject } from '@angular/core';
import { Router, UrlTree } from '@angular/router';
import { Store } from '@ngrx/store';
import { first, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { voteFormFeature } from './vote-form/state/vote-form.feature';

export function redirectIncompleteToGeneral(): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
  const router = inject(Router);
  return inject(Store).select(voteFormFeature.selectVoteFormState).pipe(
    first(state => !state.busy),
    map(state => {
      const title = state.vote?.title;
      const deadline = state.vote?.deadline;
      
      if(title && deadline) {
        return true;
      }
      return router.createUrlTree(['/vote/create/general']);
    }),
  );
}