import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { debounceTime, first } from 'rxjs';
import { map } from 'rxjs/operators';
import { authFeature } from './auth.feature';

export function ensureLoggedIn() {
  const router = inject(Router);
  return inject(Store).select(authFeature.selectAuthState).pipe(
    debounceTime(1),
    first(auth => !auth.busy),
    map(({user}) => user !== undefined),
    map(allowed => allowed || router.createUrlTree(["/"]))
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