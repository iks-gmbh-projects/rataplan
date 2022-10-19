import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {PasswordChangeModel} from "../../models/password-change.model";
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ChangePasswordService {

  constructor(private http: HttpClient) { }

  public changePassword(passwordChange: PasswordChangeModel) {
    const url = environment.authBackendURL+'users/profile/changePassword';

    return this.http.post<any>(url, passwordChange, {withCredentials: true});
  }
}
