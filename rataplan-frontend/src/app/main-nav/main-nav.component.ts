import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Observable, Subscription } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import { LoginComponent } from "../login/login.component";
import { Router } from "@angular/router";
import { MatMenuTrigger } from "@angular/material/menu";
import { Store } from "@ngrx/store";
import { LogoutAction } from "../authentication/auth.actions";
import { FrontendUser } from "../models/user.model";
import { authFeature } from '../authentication/auth.feature';
import { ProfilePasswordAuthService } from '../services/auth-guard-service/profile-password-auth-service';

@Component({
  selector: 'app-main-nav',
  templateUrl: './main-nav.component.html',
  providers: [LoginComponent],
  styleUrls: ['./main-nav.component.css']
})
export class MainNavComponent implements OnInit, OnDestroy {
  @ViewChild('trigger') trigger: MatMenuTrigger | undefined;


  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map(result => result.matches),
      shareReplay()
    );

  currentUser?: FrontendUser;
  private loggedInSub?: Subscription;


  constructor(
    private breakpointObserver: BreakpointObserver,
    private router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.loggedInSub = this.store.select(authFeature.selectUser)
      .subscribe(user => this.currentUser = user);
  }

  ngOnDestroy() {
    this.loggedInSub?.unsubscribe();
  }

  onLogout() {
    this.store.dispatch(new LogoutAction());
  }

  onClick() {
    if (!this.currentUser) {
      this.trigger?.closeMenu();
      this.router.navigateByUrl(ProfilePasswordAuthService.createUrlTree(this.router));
    } else {
      this.trigger?.openMenu();
    }
  }


}
