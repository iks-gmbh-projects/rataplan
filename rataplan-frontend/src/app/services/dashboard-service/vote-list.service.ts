import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { BackendUrlService } from "../backend-url-service/backend-url.service";
import { exhaustMap, Observable } from "rxjs";
import { VoteModel } from "../../models/vote.model";

@Injectable({
  providedIn: 'root'
})
export class VoteListService {

  constructor(
    private http: HttpClient,
    private urlService: BackendUrlService
  ) {
  }

  public getParticipatedVotes(): Observable<VoteModel[]> {
    return this.urlService.voteURL$.pipe(
      exhaustMap(url => this.http.get<VoteModel[]>(url+'users/votes/participations', { withCredentials: true }))
    );
  }

  public getCondignedVotes(): Observable<VoteModel[]> {
    return this.urlService.voteURL$.pipe(
      exhaustMap(url => this.http.get<VoteModel[]>(url+'users/votes/consigns', { withCredentials: true }))
    );
  }

  public getCreatedVotes(): Observable<VoteModel[]> {
    return this.urlService.voteURL$.pipe(
      exhaustMap(url => this.http.get<VoteModel[]>(url+'users/votes/creations', { withCredentials: true }))
    );
  }
}
