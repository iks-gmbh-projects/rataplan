import { Pipe, PipeTransform } from '@angular/core';
import { Observable } from 'rxjs';
import { DisplayNameService } from '../services/displayname-service/displayname.service';

@Pipe({
  name: 'displayName'
})
export class DisplayNamePipe implements PipeTransform {
  constructor(private displayNames: DisplayNameService) {}

  transform(userId?: string|number|null): Observable<string> {
    return this.displayNames.getDisplayName(userId);
  }

}
