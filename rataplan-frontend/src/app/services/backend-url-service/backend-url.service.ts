import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ReplaySubject, take } from 'rxjs';

type config = {
  authBackend: string,
  voteBackend: string,
  surveyBackend: string,
};

@Injectable({
  providedIn: 'root'
})
export class BackendUrlService {
  private readonly _authURL = new ReplaySubject<string>(1);
  private readonly _voteURL = new ReplaySubject<string>(1);
  private readonly _surveyURL = new ReplaySubject<string>(1);
  readonly authURL$ = this._authURL.pipe(
    take(1)
  );
  readonly voteURL$ = this._voteURL.pipe(
    take(1)
  );
  readonly surveyURL$ = this._surveyURL.pipe(
    take(1)
  );

  constructor(http: HttpClient) {
    http.get<config>(window.location.origin+'/assets/config.json')
      .subscribe({
        next: conf => {
          this._authURL.next(conf.authBackend);
          this._voteURL.next(conf.voteBackend);
          this._surveyURL.next(conf.surveyBackend);
        },
        error: console.log,
      });
  }
}
