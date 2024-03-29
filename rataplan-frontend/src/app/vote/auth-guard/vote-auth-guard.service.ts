import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable, take } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { voteFeature } from '../vote.feature';

@Injectable({
  providedIn: 'root',
})
export class VoteAuthGuard implements CanActivate {

  constructor(
    private store: Store,
    private router: Router
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.store.select(voteFeature.selectVoteState).pipe(
      filter(state => !state.busy),
      take(1),
      map(state => {
        const title = state.vote?.title;
        const deadline = state.vote?.deadline;

        if (title && deadline) {
          return true;
        }
        return this.router.createUrlTree(['/vote/create/general']);
      })
    );
  }
}
