import { TestBed, ComponentFixture } from '@angular/core/testing';
import { DebugElement } from '@angular/core';
import { NotFoundComponent } from './not-found.component';

describe('Component: Home', () => {
  let component: NotFoundComponent;
  let fixture: ComponentFixture<NotFoundComponent>;
  let de: DebugElement;
  let el: HTMLElement;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ NotFoundComponent ],
    });

    fixture = TestBed.createComponent(NotFoundComponent);
    component = fixture.componentInstance;

    de = fixture.debugElement;
    el = de.nativeElement;
  });

  it('should create an instance', () => {
    expect(component).toBeTruthy();
  });

  it('should create an instance', () => {
    expect(el.querySelector('h1').textContent.trim()).toEqual('Ouch..!');
    expect(el.querySelector('p').textContent.trim()).toEqual('Die URL konnte nicht gefunden werden ...');
    expect(el.querySelector('button').textContent.trim()).toEqual('... zurück zur Startseite');
  });


});

