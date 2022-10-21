import {Injectable} from "@angular/core";

import {HttpClient, HttpHeaders} from "@angular/common/http";
import {ProfileComponent} from "../../profile/profile.component";
import {environment} from "../../../environments/environment";

@Injectable({providedIn: "root"})

export class ChangeEmailService {

  constructor(private httpClient: HttpClient) {
  }

  changeEmail(email: String) {
    const url = environment.authBackendURL+ 'users/profile/changeEmail'

    const httpOptions = {headers: new HttpHeaders({'Content-Type': 'application/json'}), withCredentials: true};

    return this.httpClient.post<any>(url,email, httpOptions)

  }

}
