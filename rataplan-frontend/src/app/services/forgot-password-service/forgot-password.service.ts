import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { exhaustMap, first } from 'rxjs';
import { configFeature } from '../../config/config.feature';
import { nonUndefined } from '../../operators/non-empty';

@Injectable({
  providedIn: 'root',
})
export class ForgotPasswordService {
  
  constructor(
    private readonly httpClient: HttpClient,
    private readonly store: Store,
  )
  {
  }
  
  forgotPassword(mail: String) {
    return this.store.select(configFeature.selectAuthBackendUrl('users', 'forgotPassword')).pipe(
      nonUndefined,
      first(),
      exhaustMap(url => this.httpClient.post<String>(
          url,
          mail,
        ),
      ),
    );
  }
}