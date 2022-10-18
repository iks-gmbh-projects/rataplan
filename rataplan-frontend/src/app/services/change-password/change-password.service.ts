import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {PasswordChangeModel} from "../../models/password-change.model";

@Injectable({
  providedIn: 'root'
})
export class ChangePasswordService {

  constructor(private http: HttpClient) { }

  public changePassword(passwordChange: PasswordChangeModel) {
    const url = 'http://localhost:8080/v1/users/profile/changePassword';

    return this.http.post<any>(url, passwordChange, {withCredentials: true});
  }
}
