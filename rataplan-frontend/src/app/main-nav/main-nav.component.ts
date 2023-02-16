import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Observable, Subscription } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import { LoginComponent } from "../login/login.component";
import { Router } from "@angular/router";
import { MatMenuTrigger } from "@angular/material/menu";
import { Store } from "@ngrx/store";
import { appState } from "../app.reducers";
import { LogoutAction } from "../authentication/auth.actions";
import { FrontendUser } from "../models/user.model";

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
    private store: Store<appState>
  ) {}

  ngOnInit() {
    this.loggedInSub = this.store.select("auth")
      .subscribe(auth => this.currentUser = auth.user);
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
      this.router.navigateByUrl("/login")
    } else {
      this.trigger?.openMenu();
    }
  }


}
