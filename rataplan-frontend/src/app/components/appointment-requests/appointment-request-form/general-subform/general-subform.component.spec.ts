/*import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
/!* tslint:disable:no-unused-variable *!/
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GeneralSubformComponent } from './general-subform.component';
import {MyDatePickerModule} from 'mydatepicker';
import {DatepickerOptionsService} from '../../../../services/datepicker-service/datepicker-options.service';
import {CustomValidators} from '../../../../utilities/custom-validators';
import {DebugElement} from "@angular/core";
import {By} from "@angular/platform-browser";

class DatepickerOptionsServicMock {

  public setDeadline(date: Date) {

  }

}

fdescribe('GeneralSubformComponent', () => {
  let component: GeneralSubformComponent;
  let fixture: ComponentFixture<GeneralSubformComponent>;
  let de: DebugElement;
  let el: HTMLElement;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ GeneralSubformComponent ],
      providers: [ {provide: DatepickerOptionsService, useClass: DatepickerOptionsServicMock} ],
      imports: [ ReactiveFormsModule, MyDatePickerModule ]
    });

    fixture = TestBed.createComponent(GeneralSubformComponent);
    component = fixture.componentInstance;


    let formBuilder: FormBuilder = new FormBuilder();
    let general: FormGroup;

    general = formBuilder.group({
      title: ['', Validators.required],
      description: '',
      organizerMail: ['', CustomValidators.email],
      deadline: ['', Validators.required]
    });

    component.general = general;
    de = fixture.debugElement;
    el = de.nativeElement;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should asdasd', () => {
    let titleInput: HTMLInputElement;
    titleInput = de.query(By.css('#title')).nativeElement;
    titleInput.value = 'asd';

    titleInput.dispatchEvent(new Event('input'));

    fixture.detectChanges();
    console.log(component['general'].value.title)
    expect(titleInput.value).toEqual('hihi');
  });
});
*/