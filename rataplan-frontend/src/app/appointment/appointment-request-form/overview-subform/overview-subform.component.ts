import {Component, Injectable, OnInit} from '@angular/core';
import {ConfigSubformComponent} from "../config-subform/config-subform.component";
import {Router} from "@angular/router";

@Component({
  selector: 'app-overview-subform',
  templateUrl: './overview-subform.component.html',
  styleUrls: ['./overview-subform.component.css']
})
@Injectable({
  providedIn: 'root'
})
export class OverviewSubformComponent implements OnInit {
  isPageValid = true;



  constructor(public config: ConfigSubformComponent,
              private router: Router) { }

  ngOnInit(): void {
  }

  startDate = this.config.startDate;
  startTime = this.config.startTime;
  description = this.config.description;
  link = this.config.url;
  endDate = this.config.endDate;
  endTime = this.config.endTime;


  backPage(){
    this.router.navigateByUrl("create-vote/configurationOptions")
  }
  nextPage(){
    this.router.navigateByUrl("create-vote/email")
  }
}
