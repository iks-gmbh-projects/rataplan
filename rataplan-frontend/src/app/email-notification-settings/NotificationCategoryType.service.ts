import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, switchMap } from 'rxjs';
import { shareReplay } from 'rxjs/operators';
import { BackendUrlService } from '../services/backend-url-service/backend-url.service';

@Injectable({
  providedIn: 'root',
})
export class NotificationCategoryTypeService {
  readonly categoryTypes$: Observable<Record<string, string[]>>;
  constructor(
    private readonly http: HttpClient,
    private readonly urls: BackendUrlService,
  ) {
    this.categoryTypes$ = this.urls.authBackendURL('notifications', 'list-settings').pipe(
      switchMap(url => this.http.get<Record<string, string[]>>(url)),
      shareReplay(1),
    );
  }
}