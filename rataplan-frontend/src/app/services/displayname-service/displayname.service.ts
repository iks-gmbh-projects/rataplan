import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { EMPTY, Observable } from "rxjs";
import { environment } from "src/environments/environment";

@Injectable({
  providedIn: "root",
})
export class DisplayNameService {
  constructor(private http: HttpClient) {}

  public getDisplayName(userId?: string|number|null): Observable<string> {
    if(userId === undefined || userId === null) return EMPTY;
    return this.http.get(environment.authBackendURL+"users/displayName/"+userId, {
      responseType: "text",
    });
  }
}