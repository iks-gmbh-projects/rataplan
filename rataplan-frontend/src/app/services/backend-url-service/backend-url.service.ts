import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { environment } from "../../../environments/environment";
import { ReplaySubject, take } from "rxjs";

type config = {
  authBackend: string,
  appointmentBackend: string,
  surveyBackend: string,
};

@Injectable({
  providedIn: 'root'
})
export class BackendUrlService {
  private readonly _authURL = new ReplaySubject<string>(1);
  private readonly _appointmentURL = new ReplaySubject<string>(1);
  private readonly _surveyURL = new ReplaySubject<string>(1);
  readonly authURL$ = this._authURL.pipe(
    take(1)
  );
  readonly appointmentURL$ = this._authURL.pipe(
    take(1)
  );
  readonly surveyURL$ = this._authURL.pipe(
    take(1)
  );

  constructor(http: HttpClient) {
    if (environment.production) {
      http.get<config>(window.location.origin)
        .subscribe(conf => {
          this._authURL.next(conf.authBackend);
          this._appointmentURL.next(conf.appointmentBackend);
          this._surveyURL.next(conf.surveyBackend);
        })
    } else {
      this._authURL.next(environment.authBackendURL);
      this._appointmentURL.next(environment.rataplanBackendURL);
      this._surveyURL.next(environment.surveyBackendURL);
    }
  }
}
