import { Component } from '@angular/core';
import { NavigationCancel, NavigationEnd, NavigationError, NavigationStart, Router } from '@angular/router';
import { delay, Observable, of, scan } from 'rxjs';
import { switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  loading$: Observable<boolean>;
  
  constructor(private router: Router) {
    this.loading$ = router.events.pipe(
      scan((state, event) => {
        switch(true) {
        case event instanceof NavigationStart:
          return true;
        case event instanceof NavigationEnd:
        case event instanceof NavigationCancel:
        case event instanceof NavigationError:
          return false;
        default:
          return state;
        }
      }, false),
      switchMap(t => t ? of(t).pipe(delay(1000)) : of(t)),
    );
  }
}