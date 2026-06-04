import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExpedicaoPanel } from './expedicao-panel';

describe('ExpedicaoPanel', () => {
  let component: ExpedicaoPanel;
  let fixture: ComponentFixture<ExpedicaoPanel>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExpedicaoPanel],
    }).compileComponents();

    fixture = TestBed.createComponent(ExpedicaoPanel);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
