import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StatusBadges } from './status-badges';

describe('StatusBadges', () => {
  let component: StatusBadges;
  let fixture: ComponentFixture<StatusBadges>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StatusBadges],
    }).compileComponents();

    fixture = TestBed.createComponent(StatusBadges);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
