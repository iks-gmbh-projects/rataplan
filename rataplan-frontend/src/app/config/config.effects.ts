import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { createEffect } from '@ngrx/effects';
import { map } from 'rxjs/operators';
import { configActions } from './config.actions';
import { Config } from './config.reducer';

@Injectable({
  providedIn: 'root',
})
export class ConfigEffects {
  constructor(
    private readonly http: HttpClient,
  ) {
  }
  
  fetchConfig = createEffect(() =>
    this.http.get<Config>(window.location.origin + '/assets/config.json').pipe(
      map(config => configActions.fetchSuccess({config})),
    )
  );
}