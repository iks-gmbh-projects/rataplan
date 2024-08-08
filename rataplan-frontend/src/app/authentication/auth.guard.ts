import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { debounceTime, first } from 'rxjs';
import { map } from 'rxjs/operators';
import { authFeature } from './auth.feature';

export function getURL(route: ActivatedRouteSnapshot): string {
  return route.pathFromRoot.flatMap(v => v.url).map(s => s.toString()).join('/') +
  (route.queryParams ? `?${Object.entries(route.queryParams).map(([k, v]) => `${k}=${v}`).join('&')}` : '') +
  (route.fragment ? `#${route.fragment}` : '')
}

export function ensureLoggedIn(next: ActivatedRouteSnapshot) {
  const router = inject(Router);
  return inject(Store).select(authFeature.selectAuthState).pipe(
    debounceTime(1),
    first(auth => !auth.busy),
    map(({user}) => user !== undefined),
    map(allowed => allowed || router.createUrlTree(["/login"], {
      queryParams: {
        redirect: getURL(next),
      },
    })),
  );
}

export function ensureLoggedOut() {
  const router = inject(Router);
  return inject(Store).select(authFeature.selectAuthState).pipe(
    debounceTime(1),
    first(auth => !auth.busy),
    map(({user}) => user === undefined),
    map(allowed => allowed || router.createUrlTree(["/"]))
  );
}