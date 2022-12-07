import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {PasswordChangeModel} from "../../models/password-change.model";
import { BackendUrlService } from "../backend-url-service/backend-url.service";
import { exhaustMap } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ChangePasswordService {

  constructor(private http: HttpClient, private urlService: BackendUrlService) { }

  public changePassword(passwordChange: PasswordChangeModel) {
    return this.urlService.authURL$.pipe(
      exhaustMap(authURL => {
        const url = authURL + 'users/profile/changePassword';

        return this.http.post<any>(url, passwordChange, {withCredentials: true});
      })
    );
  }
}
