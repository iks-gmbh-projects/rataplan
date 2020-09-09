import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowTheadComponent } from './show-thead.component';

xdescribe('ShowTheadComponent', () => {
  let component: ShowTheadComponent;
  let fixture: ComponentFixture<ShowTheadComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ShowTheadComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ShowTheadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
