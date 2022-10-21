import {Component, ViewChild} from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Observable } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import {LoginComponent} from "../login/login.component";
import {LocalstorageService} from "../services/localstorage-service/localstorage.service";
import {Router} from "@angular/router";
import {MatMenuTrigger} from "@angular/material/menu";
import {LoginService} from "../services/login.service/login.service";

@Component({
  selector: 'app-main-nav',
  templateUrl: './main-nav.component.html',
  providers: [LoginComponent],
  styleUrls: ['./main-nav.component.css']
})
export class MainNavComponent {
  @ViewChild('trigger') trigger: MatMenuTrigger | undefined;


  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map(result => result.matches),
      shareReplay()
    );



  constructor(private breakpointObserver: BreakpointObserver,
               public localstorageService: LocalstorageService,
              public loginService: LoginService,
              private router: Router) {

  }
  onLogout() {
    this.loginService.logoutUser()

    localStorage.clear()

 }
  onClick(){
    if (!this.localstorageService.isLoggedIn()){
      this.trigger?.closeMenu();
      this.router.navigateByUrl("/login")
    } else {
      this.trigger?.openMenu();
    }
  }



}
