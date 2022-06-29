import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ContactData} from "../../legals/contact/contact.component";

@Injectable({
  providedIn: 'root'
})
export class ContactService {

  constructor(private http: HttpClient) { }

  public contact(contact: ContactData){
    let url = 'http://localhost:8080/v1/contacts';

    return this.http.post<any>(url, contact);
  }
}
