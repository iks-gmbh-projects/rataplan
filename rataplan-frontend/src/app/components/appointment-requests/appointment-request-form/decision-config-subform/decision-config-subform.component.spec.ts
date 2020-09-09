import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DecisionConfigSubformComponent } from './decision-config-subform.component';

xdescribe('DecisionConfigSubformComponent', () => {
  let component: DecisionConfigSubformComponent;
  let fixture: ComponentFixture<DecisionConfigSubformComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DecisionConfigSubformComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DecisionConfigSubformComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
