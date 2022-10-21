import {Injectable} from "@angular/core";

import {HttpClient, HttpHeaders} from "@angular/common/http";
import {ProfileComponent} from "../../profile/profile.component";

@Injectable({providedIn: "root"})

export class ChangeEmailService {

  constructor(private httpClient: HttpClient) {
  }

  changeEmail(email: String) {
    const url = 'http://localhost:8080/v1/users/profile/changeEmail'

    const httpOptions = {headers: new HttpHeaders({'Content-Type': 'application/json'}), withCredentials: true};

    return this.httpClient.post<any>(url,email, httpOptions)

  }

}
