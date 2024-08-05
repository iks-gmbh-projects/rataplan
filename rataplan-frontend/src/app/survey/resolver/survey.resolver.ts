import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { catchError, EMPTY, Observable } from 'rxjs';
import { Survey } from '../survey.model';
import { SurveyService } from '../survey.service';

export function resolveSurveyByAccessID(
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot,
): Survey | Observable<Survey> | Promise<Survey> {
  const router = inject(Router);
  return inject(SurveyService).getSurveyForCreator(route.params['accessID'])
    .pipe(catchError(err => {
      switch(err.status) {
      case 401:
        router.navigate(['/login'], {
          queryParams: {
            redirect: state.url,
          },
        });
        return EMPTY;
      case 403:
        router.navigate(['/survey', 'forbidden']);
        return EMPTY;
      case 404:
        router.navigate(['/survey', 'missing']);
        return EMPTY;
      }
      router.navigate(['/survey', 'unknown']);
      return EMPTY;
    }));
}

export function resolveSurveyByParticipationID(
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot,
): Survey | Observable<Survey> | Promise<Survey> {
  const router = inject(Router);
  return inject(SurveyService).getSurveyForParticipation(route.params['participationID'])
    .pipe(catchError(err => {
      switch(err.status) {
      case 401:
        router.navigate(['/login'], {
          queryParams: {
            redirect: state.url,
          },
        });
        return EMPTY;
      case 403:
        router.navigate(['/survey', 'closed']);
        return EMPTY;
      case 404:
        router.navigate(['/survey', 'missing']);
        return EMPTY;
      }
      router.navigate(['/survey', 'unknown']);
      return EMPTY;
    }));
}