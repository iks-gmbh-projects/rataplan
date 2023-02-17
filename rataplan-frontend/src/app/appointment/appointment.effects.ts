import { Injectable } from "@angular/core";
import { Actions, Effect, ofType } from "@ngrx/effects";
import { appState } from "../app.reducers";
import { Store } from "@ngrx/store";
import {
  AppointmentActions,
  InitAppointmentRequestAction,
  InitAppointmentRequestErrorAction,
  InitAppointmentRequestSuccessAction,
  PostAppointmentRequestErrorAction,
  PostAppointmentRequestSuccessAction
} from "./appointment.actions";
import { catchError, exhaustMap, from, of, switchMap, take } from "rxjs";
import { BackendUrlService } from "../services/backend-url-service/backend-url.service";
import { HttpClient } from "@angular/common/http";
import { filter, map } from "rxjs/operators";
import { AppointmentRequestModel } from "../models/appointment-request.model";
import { ActivatedRoute, Router } from "@angular/router";

@Injectable({
  providedIn: "root",
})
export class AppointmentRequestEffects {
  constructor(
    private readonly actions$: Actions,
    private readonly store: Store<appState>,
    private readonly http: HttpClient,
    private readonly router: Router,
    private readonly activeRoute: ActivatedRoute,
    private readonly urlService: BackendUrlService
  ) {
  }

  @Effect()
  initAppointmentRequest = this.actions$.pipe(
    ofType(AppointmentActions.INIT),
    switchMap((action: InitAppointmentRequestAction) => {
      if(!action.id) return of(new InitAppointmentRequestSuccessAction({
        title: "",
        deadline: "",
        appointmentRequestConfig: {
          appointmentConfig: {
            startDate: true,
            startTime: false,
            endDate: false,
            endTime: false,
            description: false,
            url: false,
          },
          decisionType: "DEFAULT",
        },
        appointments: [],
        appointmentMembers: [],
        consigneeList: [],
      }));
      else return this.urlService.appointmentURL$.pipe(
        exhaustMap(url => this.http.get<AppointmentRequestModel>(url+"/appointmentRequests/edit/"+action.id)),
        map(request => new InitAppointmentRequestSuccessAction(request)),
        catchError(err => of(new InitAppointmentRequestErrorAction(err)))
      );
    })
  );

  @Effect()
  postAppointmentRequest = this.actions$.pipe(
    ofType(AppointmentActions.POST),
    switchMap(() => this.store.select("appointmentRequest").pipe(take(1))),
    filter(state => state.complete),
    map(state => state.appointmentRequest!),
    exhaustMap(request => this.store.select("auth").pipe(
      filter(authState => !authState.busy),
      take(1),
      map(() => request)
    )),
    switchMap(request => this.urlService.appointmentURL$.pipe(
      exhaustMap(url => {
        if(request.editToken) return this.http.put<AppointmentRequestModel>(url + "/appointmentRequests/edit/"+request.editToken, request, {withCredentials: true});
        return this.http.post<AppointmentRequestModel>(url + "/appointmentRequests", request, {withCredentials: true});
      }),
      map(created => new PostAppointmentRequestSuccessAction(created)),
      catchError(err => of(new PostAppointmentRequestErrorAction(err)))
    ))
  );

  @Effect({
    dispatch: false,
  })
  successFullPost = this.actions$.pipe(
    ofType(AppointmentActions.POST_SUCCESS),
    map((action: PostAppointmentRequestSuccessAction) => this.router.navigate(["..", "links"], {
      relativeTo: this.activeRoute,
      queryParams: {
        participationToken: action.created.participationToken,
        editToken: action.created.editToken,
      },
    })),
    switchMap(from)
  )
}
