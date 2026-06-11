import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EstoqueCell } from './estoque-cell.component';

describe('EstoqueCell', () => {
  let component: EstoqueCell;
  let fixture: ComponentFixture<EstoqueCell>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EstoqueCell],
    }).compileComponents();

    fixture = TestBed.createComponent(EstoqueCell);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
