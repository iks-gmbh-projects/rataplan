import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-privacy',
  templateUrl: './privacy.component.html',
  styleUrls: ['./privacy.component.css']
})
export class PrivacyComponent implements OnInit {

  public url: any;

  constructor() { }

  ngOnInit(): void {
    this.url = "https://drumdibum.de";
  }

}
