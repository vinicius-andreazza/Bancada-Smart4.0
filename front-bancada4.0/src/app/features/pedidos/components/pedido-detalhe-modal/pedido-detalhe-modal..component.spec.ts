import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PedidoDetalheModalComponent } from './pedido-detalhe-modal.component';
import { Pedido } from '../../../../core/models/pedido.model';

describe('PedidoDetalheModalComponent', () => {
  let component: PedidoDetalheModalComponent;
  let fixture: ComponentFixture<PedidoDetalheModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PedidoDetalheModalComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(PedidoDetalheModalComponent);
    component = fixture.componentInstance;
    const pedido = {
      id: 1,
      codPedido: 1,
      dataCriacao: '2026-01-01T00:00:00',
      dataEntrada: null,
      idExpedicao: 1,
      blocos: [],
    } as unknown as Pedido;
    fixture.componentRef.setInput('pedido', pedido);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
