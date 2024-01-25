import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupDisplayComponent } from './group-display.component';

describe('GroupDisplayComponent', () => {
  let component: GroupDisplayComponent;
  let fixture: ComponentFixture<GroupDisplayComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GroupDisplayComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GroupDisplayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
