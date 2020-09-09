import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProtectorComponent } from './protector.component';

xdescribe('ProtectorComponent', () => {
  let component: ProtectorComponent;
  let fixture: ComponentFixture<ProtectorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProtectorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProtectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
