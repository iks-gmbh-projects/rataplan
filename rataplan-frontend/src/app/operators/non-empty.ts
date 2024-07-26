import { filter, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export function nonUndefined<T>(obs: Observable<T>): Observable<T extends undefined ? never : T> {
  return obs.pipe(filter(v => v !== undefined)) as Observable<T extends undefined ? never : T>;
}

export function nonNull<T>(obs: Observable<T>): Observable<T extends null ? never : T> {
  return obs.pipe(filter(v => v !== null)) as Observable<T extends null ? never : T>;
}

export function defined<T>(obs: Observable<T>): Observable<NonNullable<T>> {
  return obs.pipe(
    filter(v => v !== null && v !== undefined),
    map(v => v!),
  );
}