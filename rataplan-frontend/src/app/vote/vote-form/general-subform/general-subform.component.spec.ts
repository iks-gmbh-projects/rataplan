import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GeneralSubformComponent } from './general-subform.component';

describe('GeneralSubformComponent', () => {
  let component: GeneralSubformComponent;
  let fixture: ComponentFixture<GeneralSubformComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GeneralSubformComponent],
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GeneralSubformComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
