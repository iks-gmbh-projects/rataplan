import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, combineLatestWith, debounceTime, of, switchMap } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthActions } from '../authentication/auth.actions';
import { AllContacts } from '../models/contact.model';
import { BackendUrlService } from '../services/backend-url-service/backend-url.service';
import { contactActions } from './contacts.actions';

@Injectable({
  providedIn: 'root',
})
export class ContactsEffects {
  constructor(
    private actions$: Actions,
    private httpClient: HttpClient,
    private urlService: BackendUrlService,
  )
  {
  }
  
  fetchOnAuth = createEffect(() => this.actions$.pipe(
    ofType(AuthActions.LOGIN_SUCCESS_ACTION),
    map(() => contactActions.fetch()),
  ));
  
  fetch = createEffect(() => this.actions$.pipe(
    ofType(contactActions.fetch),
    debounceTime(50),
    combineLatestWith(this.urlService.authBackendURL('contacts')),
    switchMap(([, url]) => this.httpClient.get<AllContacts>(url, {
      withCredentials: true,
    }).pipe(
      map(allContacts => contactActions.fetchSuccess({contacts: allContacts})),
      catchError(error => of(contactActions.error({error}))),
    )),
  ));
  
  refetch = createEffect(() => this.actions$.pipe(
    ofType(contactActions.changeSuccess),
    map(() => contactActions.fetch()),
  ));
  
  createContact = createEffect(() => this.actions$.pipe(
    ofType(contactActions.createContact),
    combineLatestWith(this.urlService.authBackendURL('contacts')),
    switchMap(([{userId}, url]) => this.httpClient.post(url, userId, {
      withCredentials: true,
    }).pipe(
      map(() => contactActions.changeSuccess()),
      catchError(error => of(contactActions.error({error}))),
    )),
  ));
}