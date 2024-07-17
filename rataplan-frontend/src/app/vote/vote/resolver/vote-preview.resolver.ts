import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { Store } from '@ngrx/store';
import { catchError, EMPTY, filter, map, Observable, take, timeout } from 'rxjs';

import { VoteModel } from '../../../models/vote.model';
import { voteFeature } from '../../vote.feature';

@Injectable({
  providedIn: 'root',
})
export class VotePreviewResolver  {

  constructor(
    private router: Router,
    private store: Store,
  ) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<VoteModel> {
    return this.store.select(voteFeature.selectVoteState)
      .pipe(
        filter(state => state.complete!),
        map(state => state.vote!),
        take(1),
        timeout(100),
        catchError(() => {
          const editToken = route.parent!.params['id'];
          if(editToken) this.router.navigate(['/vote/edit', editToken]);
          else this.router.navigate(['/create-vote']);
          return EMPTY;
        }),
      );
  }
}
