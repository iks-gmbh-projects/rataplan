import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

import { ContactData } from '../../legals/contact/contact.component';

@Injectable({
  providedIn: 'root'
})
export class ContactService {

  constructor(private http: HttpClient) { }

  public contact(contact: ContactData){
    const url = environment.rataplanBackendURL+'contacts';

    return this.http.post<any>(url, contact);
  }
}
