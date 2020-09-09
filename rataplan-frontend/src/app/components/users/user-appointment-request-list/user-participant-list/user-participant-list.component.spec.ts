import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserParticipantComponent } from './user-participant-list.component';

xdescribe('UserParticipantComponent', () => {
  let component: UserParticipantComponent;
  let fixture: ComponentFixture<UserParticipantComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserParticipantComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserParticipantComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
