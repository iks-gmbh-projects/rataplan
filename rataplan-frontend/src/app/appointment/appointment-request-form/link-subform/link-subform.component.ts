import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { ActivatedRoute } from "@angular/router";

@Component({
  selector: 'app-link-subform',
  templateUrl: './link-subform.component.html',
  styleUrls: ['./link-subform.component.css'],
})
export class LinkSubformComponent implements OnInit, OnDestroy {
  participationLink = '/vote/';
  editLink = '/vote/edit/';
  private sub?: Subscription;

  constructor(
    private activateRoute: ActivatedRoute
  ){
  }

  ngOnInit(): void {
    this.participationLink = '/vote/'+ this.activateRoute.snapshot.queryParams['participationToken'];
    this.editLink = '/vote/edit/'+ this.activateRoute.snapshot.queryParams['editToken'];
    this.sub = this.activateRoute.queryParams.subscribe(params => {
      this.participationLink = '/vote/'+ params['participationToken'];
      this.editLink = '/vote/edit/'+ params['editToken'];
    })
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

}
