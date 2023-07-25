import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ConfirmAccountService } from '../confirm-account-service';

@Component({
  selector: 'app-confirm-account',
  templateUrl: './confirm-account.component.html',
  styleUrls: ['./confirm-account.component.css']
})
export class ConfirmAccountComponent implements OnInit {

  constructor(private route: ActivatedRoute, private confirmAccountService: ConfirmAccountService) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => this.confirmAccountService.confirmAccount(params['token']));
  }


}
