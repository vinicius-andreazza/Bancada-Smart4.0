import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ToastNotifications } from './toast-notifications.component';

describe('ToastNotifications', () => {
  let component: ToastNotifications;
  let fixture: ComponentFixture<ToastNotifications>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ToastNotifications],
    }).compileComponents();

    fixture = TestBed.createComponent(ToastNotifications);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('text', 'mensagem de teste');
    fixture.componentRef.setInput('variant', 'info');
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
