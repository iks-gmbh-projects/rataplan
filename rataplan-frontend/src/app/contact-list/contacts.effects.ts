import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { catchError, combineLatestWith, debounceTime, first, of, switchMap } from 'rxjs';
import { map } from 'rxjs/operators';
import { authActions } from '../authentication/auth.actions';
import { configFeature } from '../config/config.feature';
import { AllContacts } from '../models/contact.model';
import { nonUndefined } from '../operators/non-empty';
import { contactActions } from './contacts.actions';

@Injectable({
  providedIn: 'root',
})
export class ContactsEffects {
  constructor(
    private readonly actions$: Actions,
    private readonly httpClient: HttpClient,
    private readonly store: Store,
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
    combineLatestWith(this.store.select(configFeature.selectAuthBackendUrl('contacts')).pipe(nonUndefined)),
    switchMap(([, url]) => this.httpClient.get<AllContacts>(url, {
      withCredentials: true,
    }).pipe(
      map(allContacts => contactActions.fetchSuccess({contacts: allContacts})),
      catchError(error => of(contactActions.error(error))),
    )),
  ));
  
  refetch = createEffect(() => this.actions$.pipe(
    ofType(contactActions.changeSuccess),
    map(() => contactActions.fetch()),
  ));
  
  createContact = createEffect(() => this.actions$.pipe(
    ofType(contactActions.createContact),
    combineLatestWith(this.store.select(configFeature.selectAuthBackendUrl('contacts')).pipe(nonUndefined)),
    switchMap(([{userId}, url]) => this.httpClient.post(url, userId, {
      withCredentials: true,
    }).pipe(
      map(() => contactActions.changeSuccess()),
      catchError(error => of(contactActions.error(error))),
    )),
  ));
  
  deleteContact = createEffect(() => this.actions$.pipe(
    ofType(contactActions.deleteContact),
    switchMap(({userId}) => this.store.select(configFeature.selectAuthBackendUrl('contacts', userId)).pipe(nonUndefined, first())),
    switchMap(url => this.httpClient.delete(url, {
      withCredentials: true,
    }).pipe(
      map(() => contactActions.changeSuccess()),
      catchError(error => of(contactActions.error(error))),
    )),
  ));
  
  createGroup = createEffect(() => this.actions$.pipe(
    ofType(contactActions.createGroup),
    combineLatestWith(this.store.select(configFeature.selectAuthBackendUrl('contacts', 'group')).pipe(nonUndefined)),
    switchMap(([{name}, url]) => this.httpClient.post(url, {name}, {
      withCredentials: true,
    }).pipe(
      map(() => contactActions.changeSuccess()),
      catchError(error => of(contactActions.error(error))),
    )),
  ));
  
  renameGroup = createEffect(() => this.actions$.pipe(
    ofType(contactActions.renameGroup),
    switchMap(({id, name}) => this.store.select(configFeature.selectAuthBackendUrl('contacts', 'group', id, 'name')).pipe(
      nonUndefined,
      first(),
      map(url => [{name}, url] as const),
    )),
    switchMap(([{name}, url]) => this.httpClient.put(url, name, {
      withCredentials: true,
    }).pipe(
      map(() => contactActions.changeSuccess()),
      catchError(error => of(contactActions.error(error))),
    )),
  ));
  
  deleteGroup = createEffect(() => this.actions$.pipe(
    ofType(contactActions.deleteGroup),
    switchMap(({id}) => this.store.select(configFeature.selectAuthBackendUrl('contacts', 'group', id)).pipe(nonUndefined, first())),
    switchMap(url => this.httpClient.delete(url, {
      withCredentials: true,
    }).pipe(
      map(() => contactActions.changeSuccess()),
      catchError(error => of(contactActions.error(error))),
    )),
  ));
  
  addContactToGroup = createEffect(() => this.actions$.pipe(
    ofType(contactActions.addToGroup),
    switchMap(({groupId, contactId}) => this.store.select(configFeature.selectAuthBackendUrl('contacts', 'group', groupId, 'contact')).pipe(
      nonUndefined,
      first(),
      map(url => [{contactId}, url] as const),
    )),
    switchMap(([{contactId}, url]) => this.httpClient.post(url, contactId, {
      withCredentials: true,
    }).pipe(
      map(() => contactActions.changeSuccess()),
      catchError(error => of(contactActions.error(error))),
    )),
  ));
  removeContactFromGroup = createEffect(() => this.actions$.pipe(
    ofType(contactActions.removeFromGroup),
    switchMap(({groupId, contactId}) => this.store.select(configFeature.selectAuthBackendUrl(
      'contacts',
      'group',
      groupId,
      'contact',
      contactId,
    )).pipe(nonUndefined, first())),
    switchMap(url => this.httpClient.delete(url, {
      withCredentials: true,
    }).pipe(
      map(() => contactActions.changeSuccess()),
      catchError(error => of(contactActions.error(error))),
    )),
  ));
}