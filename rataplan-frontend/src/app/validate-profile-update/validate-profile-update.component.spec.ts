import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ValidateProfileUpdateComponent } from './validate-profile-update.component';

describe('ValidateProfileUpdateComponent', () => {
  let component: ValidateProfileUpdateComponent;
  let fixture: ComponentFixture<ValidateProfileUpdateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ValidateProfileUpdateComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ValidateProfileUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
