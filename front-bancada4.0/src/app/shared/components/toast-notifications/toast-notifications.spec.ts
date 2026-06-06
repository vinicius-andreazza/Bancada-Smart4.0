import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ToastNotifications } from './toast-notifications';

describe('ToastNotifications', () => {
  let component: ToastNotifications;
  let fixture: ComponentFixture<ToastNotifications>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ToastNotifications],
    }).compileComponents();

    fixture = TestBed.createComponent(ToastNotifications);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
