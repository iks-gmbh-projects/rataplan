import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EmailSubformComponent } from './email-subform.component';

xdescribe('EmailSubformComponent', () => {
  let component: EmailSubformComponent;
  let fixture: ComponentFixture<EmailSubformComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EmailSubformComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmailSubformComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
