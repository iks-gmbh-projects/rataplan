import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { exhaustMap, first } from 'rxjs';
import { configFeature } from '../../config/config.feature';
import { PasswordChangeModel } from '../../models/password-change.model';
import { nonUndefined } from '../../operators/non-empty';

@Injectable({
  providedIn: 'root',
})
export class ChangePasswordService {
  
  constructor(
    private readonly http: HttpClient,
    private readonly store: Store,
  )
  {
  }
  
  public changePassword(passwordChange: PasswordChangeModel) {
    return this.store.select(configFeature.selectAuthBackendUrl('users', 'profile', 'changePassword')).pipe(
      nonUndefined,
      first(),
      exhaustMap(url => this.http.post<any>(url, passwordChange, {withCredentials: true})),
    );
  }
}