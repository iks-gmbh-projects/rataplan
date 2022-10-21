import {Component, OnInit} from '@angular/core';
import {LoginService} from "./services/login.service/login.service";
import { isUndefined, isNullOrUndefined } from 'is-what';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent  implements OnInit{
  title = 'drumdibum-rework';

  ngOnInit() {
    this.loginService.getUserData()
  }

  constructor(private loginService: LoginService) {
    if (!isNullOrUndefined(localStorage.getItem('username'))) {
      loginService.getUserData().subscribe(res => {


      }, error => {
        localStorage.clear()
      })

    }
  }


}
