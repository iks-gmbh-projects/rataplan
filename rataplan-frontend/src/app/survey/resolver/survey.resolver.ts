import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from "@angular/router";
import { catchError, EMPTY, Observable } from "rxjs";
import { Survey } from "../survey.model";
import { SurveyService } from "../survey.service";

@Injectable()
export class AccessIDSurveyResolver implements Resolve<Survey> {
  constructor(private surveys: SurveyService, private router: Router) {
  }

  public resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Survey | Observable<Survey> | Promise<Survey> {
    return this.surveys.getSurveyForCreator(route.params["accessID"])
      .pipe(catchError(err => {
        switch (err.status) {
          case 401:
            this.router.navigate(["/login"], {
              queryParams: {
                redirect: state.url,
              },
            });
            return EMPTY;
          case 403:
            this.router.navigate(["/survey", "forbidden"]);
            return EMPTY;
          case 404:
            this.router.navigate(["/survey", "missing"]);
            return EMPTY;
        }
        this.router.navigate(["/survey", "unknown"]);
        return EMPTY;
      }));
  }
}

@Injectable()
export class ParticipationIDSurveyResolver implements Resolve<Survey> {
  constructor(private surveys: SurveyService, private router: Router) {
  }

  public resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Survey | Observable<Survey> | Promise<Survey> {
    return this.surveys.getSurveyForParticipation(route.params["participationID"])
      .pipe(catchError(err => {
        switch (err.status) {
          case 401:
            this.router.navigate(["/login"], {
              queryParams: {
                redirect: state.url,
              },
            });
            return EMPTY;
          case 403:
            this.router.navigate(["/survey", "closed"]);
            return EMPTY;
          case 404:
            this.router.navigate(["/survey", "missing"]);
            return EMPTY;
        }        this.router.navigate(["/survey", "unknown"]);
        return EMPTY;
      }));
  }
}
