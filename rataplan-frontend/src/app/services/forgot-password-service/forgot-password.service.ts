import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import { BackendUrlService } from "../backend-url-service/backend-url.service";
import { exhaustMap } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ForgotPasswordService {

  constructor(private httpClient: HttpClient, private urlService: BackendUrlService) {
  }

  forgotPassword(mail: String) {
    return this.urlService.authURL$.pipe(
      exhaustMap(authBackendURL => {
        let url = authBackendURL + 'users/forgotPassword';

        return this.httpClient.post<String>(url, mail, {headers: new HttpHeaders({"Content-Type": "application/json;charset=utf-8"})})
      })
    );
  }
}
