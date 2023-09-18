import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { BackendUrlService } from '../backend-url-service/backend-url.service';
import { exhaustMap, Observable } from 'rxjs';
import { VoteModel } from '../../models/vote.model';

@Injectable({
  providedIn: 'root',
})
export class VoteListService {
  
  constructor(
    private readonly http: HttpClient,
    private readonly urlService: BackendUrlService,
  )
  {
  }
  
  public getParticipatedVotes(): Observable<VoteModel[]> {
    return this.urlService.voteBackendURL('users', 'votes', 'participations').pipe(
      exhaustMap(url => this.http.get<VoteModel[]>(url, {withCredentials: true})),
    );
  }
  
  public getCondignedVotes(): Observable<VoteModel[]> {
    return this.urlService.voteBackendURL('users', 'votes', 'consigns').pipe(
      exhaustMap(url => this.http.get<VoteModel[]>(url, {withCredentials: true})),
    );
  }
  
  public getCreatedVotes(): Observable<VoteModel[]> {
    return this.urlService.voteBackendURL('users', 'votes', 'creations').pipe(
      exhaustMap(url => this.http.get<VoteModel[]>(url, {withCredentials: true})),
    );
  }
}
