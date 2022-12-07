import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { BackendUrlService } from "../backend-url-service/backend-url.service";
import { exhaustMap } from "rxjs";

@Injectable({providedIn: "root"})

export class ChangeDisplayNameService {

  constructor(private httpClient: HttpClient, private urlService: BackendUrlService) {
  }


  changeDisplayName(displayName: String) {
    return this.urlService.authURL$.pipe(
      exhaustMap(authURL => {
        const url = authURL + 'users/profile/changeDisplayName'

        const httpOptions = {headers: new HttpHeaders({'Content-Type': 'application/json'}), withCredentials: true};

        return this.httpClient.post<any>(url, displayName, httpOptions)
      })
    );
  }


}
