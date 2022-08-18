import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class ForgotPasswordService {

  constructor(private httpClient: HttpClient) {
  }

  forgotPassword(mail: String) {
    let url = 'http://localhost:8080/v1/users/forgotPassword';

    return this.httpClient.post<String>(url, mail, {headers: new HttpHeaders({"Content-Type": "application/json;charset=utf-8"})})
  }
}
