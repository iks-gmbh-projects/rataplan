import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangeNoteComponent } from './change-note.component';

describe('ChangeNoteComponent', () => {
  let component: ChangeNoteComponent;
  let fixture: ComponentFixture<ChangeNoteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChangeNoteComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChangeNoteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
