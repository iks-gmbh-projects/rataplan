import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { exhaustMap } from 'rxjs';

import { ContactData } from '../../legals/contact/contact.component';
import { BackendUrlService } from '../backend-url-service/backend-url.service';

@Injectable({
  providedIn: 'root',
})
export class ContactService {
  
  constructor(
    private readonly http: HttpClient,
    private readonly urlService: BackendUrlService,
  )
  {
  }
  
  public contact(contact: ContactData) {
    return this.urlService.voteBackendURL('contacts').pipe(
      exhaustMap(url => this.http.post<any>(url, contact)),
    );
  }
}
