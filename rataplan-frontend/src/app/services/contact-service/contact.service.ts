import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { ContactData } from '../../legals/contact/contact.component';
import { BackendUrlService } from "../backend-url-service/backend-url.service";
import { exhaustMap } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ContactService {

  constructor(private http: HttpClient, private urlService: BackendUrlService) { }

  public contact(contact: ContactData){
    return this.urlService.appointmentURL$.pipe(
      exhaustMap(appointmentURL => {
        const url = appointmentURL + 'contacts';

        return this.http.post<any>(url, contact);
      })
    );
  }
}
