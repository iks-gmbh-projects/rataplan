import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MobileShowComponent } from './mobile-show.component';

xdescribe('MobileDeviceComponent', () => {
  let component: MobileShowComponent;
  let fixture: ComponentFixture<MobileShowComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MobileShowComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MobileShowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
