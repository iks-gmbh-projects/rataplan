import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OverviewSubformComponent } from './overview-subform.component';

describe('OverviewSubformComponent', () => {
  let component: OverviewSubformComponent;
  let fixture: ComponentFixture<OverviewSubformComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OverviewSubformComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OverviewSubformComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
