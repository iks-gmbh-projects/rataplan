import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SecuritySubformComponent } from './security-subform.component';

xdescribe('SecuritySubformComponent', () => {
  let component: SecuritySubformComponent;
  let fixture: ComponentFixture<SecuritySubformComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SecuritySubformComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SecuritySubformComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
