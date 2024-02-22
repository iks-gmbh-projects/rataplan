import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, of, startWith, switchMap } from 'rxjs';
import { map } from 'rxjs/operators';
import { BackendUrlService } from '../backend-url-service/backend-url.service';

export type searchStatus = {
  busy: true,
  results?: undefined|null|[],
  error?: undefined,
} | {
  busy?: false,
  results: (string|number)[],
  error?: undefined,
} | {
  busy?: false,
  results?: undefined|null|[],
  error: any,
};

@Injectable({
  providedIn: 'root'
})
export class SearchUserService {
  constructor(
    private readonly http: HttpClient,
    private readonly urlService: BackendUrlService,
  ) {}
  
  search(str: string): Observable<searchStatus> {
    return this.urlService.authBackendURL('users', 'search').pipe(
      switchMap(url => this.http.get<(string|number)[]>(url, {
        withCredentials: true,
        params: new HttpParams().set('q', str),
      }).pipe(
        map(results => ({results})),
        catchError(error => of({error})),
        startWith({busy: true} as const),
      ))
    );
  }
}