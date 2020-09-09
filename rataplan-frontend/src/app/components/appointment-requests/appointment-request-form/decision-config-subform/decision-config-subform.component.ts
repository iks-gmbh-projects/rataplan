import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'rp-decision-config-subform',
  templateUrl: './decision-config-subform.component.html',
  styleUrls: ['./decision-config-subform.component.css']
})
export class DecisionConfigSubformComponent implements OnInit {

  @Input() decisionConfiguration: FormGroup;

  constructor() { }

  ngOnInit() {
  }

}
