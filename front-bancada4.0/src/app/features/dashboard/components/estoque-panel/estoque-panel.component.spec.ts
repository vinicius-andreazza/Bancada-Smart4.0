import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EstoquePanel } from './estoque-panel.component';

describe('EstoquePanel', () => {
  let component: EstoquePanel;
  let fixture: ComponentFixture<EstoquePanel>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EstoquePanel],
    }).compileComponents();

    fixture = TestBed.createComponent(EstoquePanel);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
