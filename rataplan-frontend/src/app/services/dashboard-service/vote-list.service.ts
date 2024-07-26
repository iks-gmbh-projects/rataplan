import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';

import { exhaustMap, first, Observable } from 'rxjs';
import { configFeature } from '../../config/config.feature';
import { VoteModel } from '../../models/vote.model';
import { nonUndefined } from '../../operators/non-empty';

@Injectable({
  providedIn: 'root',
})
export class VoteListService {
  
  constructor(
    private readonly http: HttpClient,
    private readonly store: Store,
  )
  {
  }
  
  public getParticipatedVotes(): Observable<VoteModel[]> {
    return this.store.select(configFeature.selectVoteBackendUrl('users', 'votes', 'participations')).pipe(
      nonUndefined,
      first(),
      exhaustMap(url => this.http.get<VoteModel[]>(url, {withCredentials: true})),
    );
  }
  
  public getConsignedVotes(): Observable<VoteModel[]> {
    return this.store.select(configFeature.selectVoteBackendUrl('users', 'votes', 'consigns')).pipe(
      nonUndefined,
      first(),
      exhaustMap(url => this.http.get<VoteModel[]>(url, {withCredentials: true})),
    );
  }
  
  public getCreatedVotes(): Observable<VoteModel[]> {
    return this.store.select(configFeature.selectVoteBackendUrl('users', 'votes', 'creations')).pipe(
      nonUndefined,
      first(),
      exhaustMap(url => this.http.get<VoteModel[]>(url, {withCredentials: true})),
    );
  }
}