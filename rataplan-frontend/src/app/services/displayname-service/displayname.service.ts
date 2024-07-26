import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { EMPTY, exhaustMap, first, Observable } from 'rxjs';
import { configFeature } from '../../config/config.feature';
import { nonUndefined } from '../../operators/non-empty';

@Injectable({
  providedIn: 'root',
})
export class DisplayNameService {
  constructor(
    private readonly http: HttpClient,
    private readonly store: Store,
  )
  {}
  
  public getDisplayName(userId?: string | number | null): Observable<string> {
    if(userId === undefined || userId === null) return EMPTY;
    return this.store.select(configFeature.selectAuthBackendUrl('users', 'displayName', userId)).pipe(
      nonUndefined,
      first(),
      exhaustMap(url => {
        return this.http.get(url, {
          responseType: 'text',
        });
      }),
    );
  }
}