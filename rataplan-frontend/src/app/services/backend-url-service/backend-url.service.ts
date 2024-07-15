import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { filter, first, map, Observable } from 'rxjs';
import { configFeature } from '../../config/config.feature';

@Injectable({
  providedIn: 'root',
})
export class BackendUrlService {
  constructor(
    private readonly store: Store
  ) {
  }
  
  public readonly loginURL: Observable<string> = this.store.select(configFeature.selectAuthBackend).pipe(
    filter(url => !!url),
    map(url => `${url}login`),
    first(),
  );
  
  public readonly logoutURL: Observable<string> = this.store.select(configFeature.selectAuthBackend).pipe(
    filter(url => !!url),
    map(url => `${url}logout`),
    first(),
  );
  
  public authBackendURL(...path: (string | number)[]): Observable<string> {
    const p = path.map(s =>
      'string' === typeof s ?
        s.replace(/^\/+/, '')
          .replace(/\/+$/, '') :
        s,
    ).join('/');
    return this.store.select(configFeature.selectAuthBackend).pipe(
      filter(url => !!url),
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
    return this.store.select(configFeature.selectVoteBackend).pipe(
      filter(url => !!url),
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
    return this.store.select(configFeature.selectSurveyBackend).pipe(
      filter(url => !!url),
      map(url => `${url}${p}`),
      first(),
    );
  }
}