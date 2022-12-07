import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { FrontendUser } from '../../register/register.component';
import { BackendUrlService } from "../backend-url-service/backend-url.service";
import { exhaustMap } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  constructor(private http: HttpClient, private urlService: BackendUrlService) {
  }

  public registerUser(frontendUser: FrontendUser) {
    return this.urlService.authURL$.pipe(
      exhaustMap(authURL => {
        const url = authURL + 'users/register';

        return this.http.post<any>(url, frontendUser);
      })
    );
  }

  public checkIfMailExists(mail: string) {
    return this.urlService.authURL$.pipe(
      exhaustMap(authURL => {
        const url = authURL + 'users/mailExists';

        return this.http.post<string>(url, mail, {headers: new HttpHeaders({'Content-Type': 'application/json;charset=utf-8'})});
      })
    );
  }

  public checkIfUsernameExists(username: string) {
    return this.urlService.authURL$.pipe(
      exhaustMap(authURL => {
        const url = authURL + 'users/usernameExists';

        return this.http.post<string>(url, username, {headers: new HttpHeaders({'Content-Type': 'application/json;charset=utf-8'})});
      })
    );
  }
}
