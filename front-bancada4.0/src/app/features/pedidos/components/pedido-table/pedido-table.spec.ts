import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PedidoTable } from './pedido-table';

describe('PedidoTable', () => {
  let component: PedidoTable;
  let fixture: ComponentFixture<PedidoTable>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PedidoTable],
    }).compileComponents();

    fixture = TestBed.createComponent(PedidoTable);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
