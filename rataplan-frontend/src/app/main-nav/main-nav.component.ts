import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Observable, Subscription } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import { LoginComponent } from '../login/login.component';
import { Router } from '@angular/router';
import { MatMenuTrigger } from '@angular/material/menu';
import { Store } from '@ngrx/store';
import { LogoutAction } from '../authentication/auth.actions';
import { FrontendUser } from '../models/user.model';
import { authFeature } from '../authentication/auth.feature';
import { notificationFeature } from '../notification/notification.feature';
import { patchNotes } from '../version/patch.notes';
import { voteNotificationtypes } from '../vote/vote.notificationtypes';

@Component({
  selector: 'app-main-nav',
  templateUrl: './main-nav.component.html',
  providers: [LoginComponent],
  styleUrls: ['./main-nav.component.css'],
})
export class MainNavComponent implements OnInit, OnDestroy {
  @ViewChild('trigger') trigger: MatMenuTrigger | undefined;
  
  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map(result => result.matches),
      shareReplay(),
    );
  
  currentUser?: FrontendUser;
  busy: boolean = false;
  notificationCount: number = 0;
  notificationState: {[type: string]: number} = {};
  readonly notificationTypeConsignee = voteNotificationtypes.consigns;
  readonly version = Object.entries(patchNotes)
    .reduce((a, [version, {releaseDate}]) => {
      const time = releaseDate.getTime();
      if(a.time < time) {
        return {version, time};
      } else {
        return a;
      }
    }, {version: '0.0.0', time: 0}).version;
  private loggedInSub?: Subscription;
  private notificationSub?: Subscription;
  
  constructor(
    private breakpointObserver: BreakpointObserver,
    private router: Router,
    private store: Store,
  )
  {}
  
  ngOnInit() {
    this.loggedInSub = this.store.select(authFeature.selectAuthState)
      .subscribe(state => {
        this.currentUser = state.user;
        this.busy = state.busy;
      });
    this.notificationSub = this.store.select(notificationFeature.selectNotificationState)
      .subscribe(n => {
        this.notificationState = n;
        this.notificationCount = Object.values(n).reduce((a, b) => a + b, 0);
      });
  }
  
  ngOnDestroy() {
    this.loggedInSub?.unsubscribe();
    this.notificationSub?.unsubscribe();
  }
  
  onLogout() {
    this.store.dispatch(new LogoutAction());
  }
  
  onClick() {
    if(!this.currentUser) {
      this.trigger?.closeMenu();
      this.router.navigateByUrl('/login');
    } else {
      this.trigger?.openMenu();
    }
  }
  
}
