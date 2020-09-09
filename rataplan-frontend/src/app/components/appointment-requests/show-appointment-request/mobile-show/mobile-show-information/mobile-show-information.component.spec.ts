import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MobileShowInformationComponent } from './mobile-show-information.component';

xdescribe('MShowInformationComponent', () => {
  let component: MobileShowInformationComponent;
  let fixture: ComponentFixture<MobileShowInformationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MobileShowInformationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MobileShowInformationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
