import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {FrontendUser} from "../../register/register.component";

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  constructor(private http: HttpClient) { }

  public registerUser(frontendUser: FrontendUser){
    let url = 'http://localhost:8080/v1/users/register';

    return this.http.post<any>(url, frontendUser);
  }

  public checkIfMailExists(mail: String){

    let url = 'http://localhost:8080/v1/users/mailExists';

    return this.http.post<String>(url, mail, {headers: new HttpHeaders({"Content-Type" : "application/json;charset=utf-8"})});
  }

  public checkIfUsernameExists(username: String) {
    let url = 'http://localhost:8080/v1/users/usernameExists';

    return this.http.post<String>(url, username, {headers: new HttpHeaders({"Content-Type" : "application/json;charset=utf-8"})});
  }
}
