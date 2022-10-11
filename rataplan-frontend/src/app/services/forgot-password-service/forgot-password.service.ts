import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ForgotPasswordService {

  constructor(private httpClient: HttpClient) {
  }

  forgotPassword(mail: String) {
    let url = environment.rataplanBackendURL+'users/forgotPassword';

    return this.httpClient.post<String>(url, mail, {headers: new HttpHeaders({"Content-Type": "application/json;charset=utf-8"})})
  }
}
