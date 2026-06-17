import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { NavLinkComponent } from './nav-link.component';

describe('NavLinkComponent', () => {
  let component: NavLinkComponent;
  let fixture: ComponentFixture<NavLinkComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NavLinkComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(NavLinkComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('label', 'Dashboard');
    fixture.componentRef.setInput('route', '/dashboard');
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
