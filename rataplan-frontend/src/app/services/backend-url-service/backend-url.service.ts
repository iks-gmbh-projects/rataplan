import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { first, map, Observable, ReplaySubject } from 'rxjs';

type config = {
  authBackend: string,
  voteBackend: string,
  surveyBackend: string,
};

@Injectable({
  providedIn: 'root',
})
export class BackendUrlService {
  private readonly _authURL = new ReplaySubject<string>(1);
  private readonly _voteURL = new ReplaySubject<string>(1);
  private readonly _surveyURL = new ReplaySubject<string>(1);
  
  constructor(http: HttpClient) {
    http.get<config>(window.location.origin + '/assets/config.json')
      .subscribe({
        next: conf => {
          this._authURL.next(conf.authBackend);
          this._voteURL.next(conf.voteBackend);
          this._surveyURL.next(conf.surveyBackend);
        },
      });
  }
  
  public readonly loginURL: Observable<string> = this._authURL.pipe(
    map(url => `${url}login`),
    first(),
  );
  
  public authBackendURL(...path: (string | number)[]): Observable<string> {
    const p = path.map(s =>
      'string' === typeof s ?
        s.replace(/^\/+/, '')
          .replace(/\/+$/, '') :
        s,
    ).join('/');
    return this._authURL.pipe(
      map(url => `${url}v1/${p}`),
      first(),
    );
  }
  
  public voteBackendURL(...path: (string | number)[]): Observable<string> {
    const p = path.map(s =>
      'string' === typeof s ?
        s.replace(/^\/+/, '')
          .replace(/\/+$/, '') :
        s,
    ).join('/');
    return this._voteURL.pipe(
      map(url => `${url}${p}`),
      first(),
    );
  }
  
  public surveyBackendURL(...path: (string | number)[]): Observable<string> {
    const p = path.map(s =>
      'string' === typeof s ?
        s.replace(/^\/+/, '')
          .replace(/\/+$/, '') :
        s,
    ).join('/');
    return this._surveyURL.pipe(
      map(url => `${url}${p}`),
      first(),
    );
  }
}