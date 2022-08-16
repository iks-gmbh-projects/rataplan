import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LinkSubformComponent } from './link-subform.component';

describe('LinkSubformComponent', () => {
  let component: LinkSubformComponent;
  let fixture: ComponentFixture<LinkSubformComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LinkSubformComponent],
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LinkSubformComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
