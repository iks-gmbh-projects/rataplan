import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VoteOptionInfoDialogComponent } from './vote-option-info-dialog.component';

describe('VoteOptionInfoDialogComponent', () => {
  let component: VoteOptionInfoDialogComponent;
  let fixture: ComponentFixture<VoteOptionInfoDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VoteOptionInfoDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VoteOptionInfoDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
