import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MobileShowDecisionsComponent } from './mobile-show-decisions.component';

xdescribe('MobileShowDecisionsComponent', () => {
  let component: MobileShowDecisionsComponent;
  let fixture: ComponentFixture<MobileShowDecisionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MobileShowDecisionsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MobileShowDecisionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
