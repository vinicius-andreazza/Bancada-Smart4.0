import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PedidoRow } from './pedido-row.component';
import { Pedido } from '../../../../core/models/pedido.model';
import { StatusPedido } from '../../../../core/models/enums/statuspedido.enum';

const PEDIDO_FAKE = {
  id: 1,
  codPedido: 42,
  status: StatusPedido.PENDENTE,
  tipoPedido: 1,
  corTampa: 1,
  dataCriacao: '2026-01-01T08:00:00',
  dataInicio: '2026-01-01T08:05:00',
  dataEntrada: '2026-01-01T08:30:00',
  blocos: [],
} as unknown as Pedido;

describe('PedidoRow', () => {
  let component: PedidoRow;
  let fixture: ComponentFixture<PedidoRow>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PedidoRow],
    }).compileComponents();

    fixture = TestBed.createComponent(PedidoRow);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('pedido', PEDIDO_FAKE);
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
