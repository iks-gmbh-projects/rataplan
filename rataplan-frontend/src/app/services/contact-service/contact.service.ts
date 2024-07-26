import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { exhaustMap, first } from 'rxjs';
import { configFeature } from '../../config/config.feature';

import { ContactData } from '../../legals/contact/contact.component';
import { nonUndefined } from '../../operators/non-empty';

@Injectable({
  providedIn: 'root',
})
export class ContactService {
  
  constructor(
    private readonly http: HttpClient,
    private readonly store: Store,
  )
  {
  }
  
  public contact(contact: ContactData) {
    return this.store.select(configFeature.selectVoteBackendUrl('contacts')).pipe(
      nonUndefined,
      first(),
      exhaustMap(url => this.http.post<any>(url, contact)),
    );
  }
}