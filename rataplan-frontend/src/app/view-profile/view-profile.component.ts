import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';
import { authFeature } from '../authentication/auth.feature';

@Component({
  selector: 'app-view-profile',
  templateUrl: './view-profile.component.html',
  styleUrls: ['./view-profile.component.css']
})
export class ViewProfileComponent implements OnInit,OnDestroy {

  userName?: string;
  displayName?: string;
  email?: string;

  userDataSub?: Subscription;

  constructor(private store: Store) {

  }

  ngOnInit(): void {
    this.userDataSub = this.store.select(authFeature.selectUser)
      .subscribe((user) => {
        this.userName = user!.username;
        this.displayName = user!.displayname;
        this.email = user!.mail;
      });
  }

  ngOnDestroy():void {
    this.userDataSub?.unsubscribe();
  }

}
