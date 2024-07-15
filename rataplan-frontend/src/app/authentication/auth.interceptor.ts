import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { debounceTime, Observable, take } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { authFeature } from './auth.feature';

@Injectable({
  providedIn: 'root',
})
export class AuthInterceptor implements HttpInterceptor {
  constructor(
    private readonly store: Store,
  ) {}
  public intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if(req.withCredentials && !req.headers.has('Authorization')) {
      return this.store.select(authFeature.selectAuthState).pipe(
        debounceTime(1),
        map(({token}) => token),
        take(1),
        map(token => token ? req.clone({headers: req.headers.set('Authorization', `Bearer ${token}`)}) : req),
        switchMap(req => next.handle(req)),
      );
    }
    return next.handle(req);
  }
  
}