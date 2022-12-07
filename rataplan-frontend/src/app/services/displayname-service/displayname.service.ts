import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { EMPTY, exhaustMap, Observable } from "rxjs";
import { BackendUrlService } from "../backend-url-service/backend-url.service";

@Injectable({
  providedIn: "root",
})
export class DisplayNameService {
  constructor(private http: HttpClient, private urlService: BackendUrlService) {}

  public getDisplayName(userId?: string|number|null): Observable<string> {
    if(userId === undefined || userId === null) return EMPTY;
    return this.urlService.authURL$.pipe(
      exhaustMap(url => {
        return this.http.get(url + "users/displayName/" + userId, {
          responseType: "text",
        });
      })
    );
  }
}
