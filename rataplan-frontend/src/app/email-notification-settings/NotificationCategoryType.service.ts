import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { first, Observable, switchMap } from 'rxjs';
import { shareReplay } from 'rxjs/operators';
import { configFeature } from '../config/config.feature';
import { nonUndefined } from '../operators/non-empty';

@Injectable({
  providedIn: 'root',
})
export class NotificationCategoryTypeService {
  readonly categoryTypes$: Observable<Record<string, string[]>>;
  constructor(
    private readonly http: HttpClient,
    private readonly store: Store,
  ) {
    this.categoryTypes$ = this.store.select(configFeature.selectAuthBackendUrl('notifications', 'list-settings')).pipe(
      nonUndefined,
      first(),
      switchMap(url => this.http.get<Record<string, string[]>>(url)),
      shareReplay(1),
    );
  }
}