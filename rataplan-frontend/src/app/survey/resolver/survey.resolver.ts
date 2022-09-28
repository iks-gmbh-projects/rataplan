import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from "@angular/router";
import { Observable } from "rxjs";
import { Survey } from "../survey.model";
import { SurveyService } from "../survey.service";

@Injectable()
export class AccessIDSurveyResolver implements Resolve<Survey> {
  constructor(private surveys:SurveyService) {}

  public resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Survey | Observable<Survey> | Promise<Survey> {
    return this.surveys.getSurveyForCreator(route.params["accessID"]);
  }
}

@Injectable()
export class ParticipationIDSurveyResolver implements Resolve<Survey> {
  constructor(private surveys:SurveyService) {}

  public resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Survey | Observable<Survey> | Promise<Survey> {
    return this.surveys.getSurveyForParticipation(route.params["participationID"]);
  }
}