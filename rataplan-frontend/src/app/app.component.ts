import {Component, OnInit} from '@angular/core';
import {LoginService} from "./services/login.service/login.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  ngOnInit() {
    this.loginService.getUserData()
  }

  constructor(private loginService: LoginService) {
    if (localStorage.getItem('username')) {
      loginService.getUserData().subscribe(res => {


      }, error => {
        localStorage.clear()
      })

    }
  }


}
