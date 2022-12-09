import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class VoteListService {

  constructor(private http: HttpClient) {
  }

  public getParticipatedVotes(){
    const url = environment.rataplanBackendURL+'users/appointmentRequests/participations';
    return this.http.get<any>(url, { withCredentials: true });
  }

  public getCreatedVotes(){
    const url = environment.rataplanBackendURL+'users/appointmentRequests/creations';
    return this.http.get<any>(url, { withCredentials: true });
  }
}
