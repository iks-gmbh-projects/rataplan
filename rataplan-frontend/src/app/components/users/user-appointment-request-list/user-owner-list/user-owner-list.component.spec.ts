import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserOwnerListComponent } from './user-owner-list.component';

xdescribe('UserOwnerListComponent', () => {
  let component: UserOwnerListComponent;
  let fixture: ComponentFixture<UserOwnerListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserOwnerListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserOwnerListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
