import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { BackendUrlService } from "../backend-url-service/backend-url.service";
import { exhaustMap } from "rxjs";

export type deletionMethod = "DELETE"|"ANONYMIZE";
export type deletionChoices = {
  backendChoice: deletionMethod,
  surveyToolChoice: deletionMethod,
  password: string,
};

@Injectable({
  providedIn: 'root'
})
export class DeleteProfileService {

  constructor(private http: HttpClient, private urlService: BackendUrlService) { }

  public deleteProfile(choices: deletionChoices): void {
    this.urlService.authURL$.pipe(
      exhaustMap(url => {
        return this.http.delete(url+"users/profile", {
          body: choices,
          withCredentials: true,
        });
      })
    ).subscribe();
  }
}
