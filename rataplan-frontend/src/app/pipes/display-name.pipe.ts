import { HttpClient } from '@angular/common/http';
import { Pipe, PipeTransform } from '@angular/core';
import { EMPTY, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Pipe({
  name: 'displayName'
})
export class DisplayNamePipe implements PipeTransform {
  constructor(private http: HttpClient) {}

  transform(userId?: string|number|null): Observable<string> {
    if(userId === undefined || userId === null) return EMPTY;
    return this.http.get(environment.authBackendURL+"users/displayName/"+userId, {
      responseType: "text",
    });
  }

}
