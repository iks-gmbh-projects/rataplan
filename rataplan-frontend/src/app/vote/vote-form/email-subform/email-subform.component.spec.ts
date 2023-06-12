import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmailSubformComponent } from './email-subform.component';

describe('EmailSubformComponent', () => {
  let component: EmailSubformComponent;
  let fixture: ComponentFixture<EmailSubformComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EmailSubformComponent],
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EmailSubformComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
