import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { catchError, combineLatestWith, of, sample, switchMap } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { authActions } from '../../authentication/auth.actions';
import { BackendUrlService } from '../../services/backend-url-service/backend-url.service';
import { emailNotificationSettingsActions } from './email-notification-settings.actions';
import { emailNotificationSettingsFeature } from './email-notification-settings.feature';
import { EmailNotificationSettings } from './email-notification-settings.model';

@Injectable({
  providedIn: 'root',
})
export class EmailNotificationSettingsEffect {
  constructor(
    private readonly actions$: Actions,
    private readonly store: Store,
    private readonly http: HttpClient,
    private readonly urls: BackendUrlService,
  ) {}
  
  init = createEffect(() => this.actions$.pipe(
    ofType(authActions.loginSuccess),
    map(emailNotificationSettingsActions.fetch),
  ));
  
  fetch = createEffect(() => this.actions$.pipe(
    ofType(emailNotificationSettingsActions.fetch),
    switchMap(() => this.urls.authBackendURL('notifications', 'settings')),
    switchMap(url => this.http.get<EmailNotificationSettings>(url, {
      withCredentials: true,
    }).pipe(
      map(response => emailNotificationSettingsActions.success({response})),
      catchError(error => of(emailNotificationSettingsActions.error(error))),
    ))
  ));
  
  save = createEffect(() => this.store.select(emailNotificationSettingsFeature.selectEmailNotificationSettingsState).pipe(
    filter(({busy, settings}) => !busy && settings !== undefined),
    map(({settings}) => settings),
    sample(this.actions$.pipe(ofType(emailNotificationSettingsActions.saveSettings))),
    combineLatestWith(this.urls.authBackendURL('notifications', 'settings')),
    switchMap(([settings, url]) => this.http.put<EmailNotificationSettings>(
      url,
      settings,
      {
        withCredentials: true,
      }
    ).pipe(
      map(response => emailNotificationSettingsActions.success({response})),
      catchError(error => of(emailNotificationSettingsActions.error(error))),
    )),
  ));
}