import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefreshTimer } from './refresh-timer.component';

describe('RefreshTimer', () => {
  let component: RefreshTimer;
  let fixture: ComponentFixture<RefreshTimer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RefreshTimer],
    }).compileComponents();

    fixture = TestBed.createComponent(RefreshTimer);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('seconds', 10);
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
