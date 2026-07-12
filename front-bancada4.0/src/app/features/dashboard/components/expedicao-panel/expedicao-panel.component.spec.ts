import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExpedicaoPanel } from './expedicao-panel.component';

describe('ExpedicaoPanel', () => {
  let component: ExpedicaoPanel;
  let fixture: ComponentFixture<ExpedicaoPanel>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExpedicaoPanel],
    }).compileComponents();

    fixture = TestBed.createComponent(ExpedicaoPanel);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('positions', []);
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
