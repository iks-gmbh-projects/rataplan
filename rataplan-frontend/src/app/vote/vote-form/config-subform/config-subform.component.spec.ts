import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfigSubformComponent } from './config-subform.component';

describe('ConfigSubformComponent', () => {
  let component: ConfigSubformComponent;
  let fixture: ComponentFixture<ConfigSubformComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ConfigSubformComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfigSubformComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
