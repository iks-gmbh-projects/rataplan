import { Component, OnInit, Output, ViewChild, EventEmitter, Input } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

@Component({
  selector: 'rp-protector',
  templateUrl: './protector.component.html',
  styleUrls: ['./protector.component.css']
})
export class ProtectorComponent implements OnInit {

  public password;
  @Input() wrongPassword: boolean;
  @Input() isEdit: boolean;
  @Input() isLoading: boolean;

  @Output() onSubmitPassword: EventEmitter<string>;
  @ViewChild('passwordInput') passwordInput;

  public spinnerStyles = { 'padding': '10px 15px', 'width': '49px'};

  constructor() {
    this.onSubmitPassword = new EventEmitter();
  }

  ngOnInit() {
  }

  over() {
    this.passwordInput.nativeElement.type = 'text';
  }

  leave() {
    this.passwordInput.nativeElement.type = 'password';
  }

  submit() {
    this.onSubmitPassword.emit(this.password)
  }

}
