import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, combineLatestWith, debounceTime, of, switchMap } from 'rxjs';
import { map } from 'rxjs/operators';
import { authActions } from '../authentication/auth.actions';
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
  
  clearOnLogout$ = createEffect(() => this.actions$.pipe(
    ofType(authActions.logout),
    map(contactActions.reset),
  ));
  
  fetchOnAuth = createEffect(() => this.actions$.pipe(
    ofType(authActions.loginSuccess),
    map(contactActions.fetch),
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
  
  deleteContact = createEffect(() => this.actions$.pipe(
    ofType(contactActions.deleteContact),
    switchMap(({userId}) => this.urlService.authBackendURL('contacts', userId)),
    switchMap(url => this.httpClient.delete(url, {
      withCredentials: true,
    }).pipe(
      map(() => contactActions.changeSuccess()),
      catchError(error => of(contactActions.error({error}))),
    )),
  ));
  
  createGroup = createEffect(() => this.actions$.pipe(
    ofType(contactActions.createGroup),
    combineLatestWith(this.urlService.authBackendURL('contacts', 'group')),
    switchMap(([{name}, url]) => this.httpClient.post(url, {name}, {
      withCredentials: true,
    }).pipe(
      map(() => contactActions.changeSuccess()),
      catchError(error => of(contactActions.error({error}))),
    )),
  ));
  
  renameGroup = createEffect(() => this.actions$.pipe(
    ofType(contactActions.renameGroup),
    switchMap(({id, name}) => this.urlService.authBackendURL('contacts', 'group', id, 'name').pipe(
      map(url => [{name}, url] as const),
    )),
    switchMap(([{name}, url]) => this.httpClient.put(url, name, {
      withCredentials: true,
    }).pipe(
      map(() => contactActions.changeSuccess()),
      catchError(error => of(contactActions.error({error}))),
    )),
  ));
  
  deleteGroup = createEffect(() => this.actions$.pipe(
    ofType(contactActions.deleteGroup),
    switchMap(({id}) => this.urlService.authBackendURL('contacts', 'group', id)),
    switchMap(url => this.httpClient.delete(url, {
      withCredentials: true,
    }).pipe(
      map(() => contactActions.changeSuccess()),
      catchError(error => of(contactActions.error({error}))),
    )),
  ));
  
  addContactToGroup = createEffect(() => this.actions$.pipe(
    ofType(contactActions.addToGroup),
    switchMap(({groupId, contactId}) => this.urlService.authBackendURL('contacts', 'group', groupId, 'contact').pipe(
      map(url => [{contactId}, url] as const),
    )),
    switchMap(([{contactId}, url]) => this.httpClient.post(url, contactId, {
      withCredentials: true,
    }).pipe(
      map(() => contactActions.changeSuccess()),
      catchError(error => of(contactActions.error({error}))),
    )),
  ));
  removeContactFromGroup = createEffect(() => this.actions$.pipe(
    ofType(contactActions.removeFromGroup),
    switchMap(({groupId, contactId}) => this.urlService.authBackendURL(
      'contacts',
      'group',
      groupId,
      'contact',
      contactId,
    )),
    switchMap(url => this.httpClient.delete(url, {
      withCredentials: true,
    }).pipe(
      map(() => contactActions.changeSuccess()),
      catchError(error => of(contactActions.error({error}))),
    )),
  ));
}