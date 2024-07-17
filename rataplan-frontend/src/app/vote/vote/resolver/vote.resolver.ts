import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';

import { VoteModel } from '../../../models/vote.model';
import { VoteService } from '../vote-service/vote.service';

@Injectable({
  providedIn: 'root',
})
export class VoteResolver  {

  constructor(
    private router: Router,
    private VoteService: VoteService,
  ) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<VoteModel> | Promise<VoteModel> | VoteModel {
    return this.VoteService.getVoteByParticipationToken(route.params['id']);
  }
}
