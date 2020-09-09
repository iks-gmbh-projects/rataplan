import { TestBed, ComponentFixture } from '@angular/core/testing';
import { HomeComponent } from './home.component';
import { DebugElement } from '@angular/core';

describe('Component: Home', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;
  let de: DebugElement;
  let el: HTMLElement;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ HomeComponent ],
    });

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;

    de = fixture.debugElement;
    el = de.nativeElement;
  });

  it('should create an instance', () => {
    expect(component).toBeTruthy();
  });

  it('should create an instance', () => {
    expect(el.querySelector('h1').textContent.trim()).toEqual('Willkommen bei drumdibum');
    expect(el.querySelector('p').textContent.trim()).toEqual('Schnell und einfach Termine finden.');
    expect(el.querySelector('button').textContent.trim()).toEqual('Sofort loslegen');
  });


});

