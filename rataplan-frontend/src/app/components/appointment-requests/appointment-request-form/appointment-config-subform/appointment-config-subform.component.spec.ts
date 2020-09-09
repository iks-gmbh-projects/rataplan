import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AppointmentConfigSubformComponent } from './appointment-config-subform.component';

xdescribe('AppointmentConfigSubformComponent', () => {
  let component: AppointmentConfigSubformComponent;
  let fixture: ComponentFixture<AppointmentConfigSubformComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AppointmentConfigSubformComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AppointmentConfigSubformComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
