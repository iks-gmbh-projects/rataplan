import { AfterViewChecked, AfterViewInit, Component, OnInit } from '@angular/core';
import { UserService } from './services/user-service/user.service';
import { isNullOrUndefined } from 'util';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';
import { NavigationEnd, Router } from '@angular/router';

@Component({
  selector: 'rp-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, AfterViewInit, AfterViewChecked {

  private firstLoad = true;


  constructor(private userService: UserService, private router: Router) {
    if (!isNullOrUndefined(JSON.parse(localStorage.getItem('rp_username')))) {
      userService.getUserData()
        .subscribe(res => {
          },
          err => {
            localStorage.removeItem('rp_username');
            localStorage.removeItem('rp_id');
          });
    }
  }

  ngOnInit() {
    this.router.events.subscribe((event) => {
      if (!(event instanceof NavigationEnd)) {
        return;
      }
      window.scrollTo(0, 0)
    });
  }

  ngAfterViewInit() {
    let resizeSubject: Subject<any> = new Subject();
    Observable.fromEvent(window, 'resize')
      .subscribe(resizeSubject);

    resizeSubject.subscribe(res => {
      this.checkWidth();
    });
  }

  ngAfterViewChecked() {
    if (this.firstLoad) {
      this.firstLoad = false;
      this.checkWidth()
    }
  }

  checkWidth() {
    if (window.innerWidth < 768) {
      let container = document.getElementById('outlet-container');

      container.setAttribute('style', 'width: 90%')
    }
    if (window.innerWidth > 768) {
      let container = document.getElementById('outlet-container');

      container.setAttribute('style', 'width: 70%')
    }
  }

}
