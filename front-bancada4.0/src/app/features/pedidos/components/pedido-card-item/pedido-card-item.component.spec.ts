import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PedidoCardItem } from './pedido-card-item.component';
import { Pedido } from '../../../../core/models/pedido.model';
import { StatusPedido } from '../../../../core/models/enums/statuspedido.enum';
import { TipoPedido } from '../../../../core/models/enums/tipopedido.enum';
import { CorTampa } from '../../../../core/models/enums/cortampa.enum';

const mockPedido: Pedido = {
  id: 1,
  codPedido: 1001,
  clpIp: '0.0.0.0',
  dataCriacao: '2026-01-01',
  status: StatusPedido.PENDENTE,
  tipoPedido: TipoPedido.SIMPLES,
  corTampa: CorTampa.AZUL,
  dataEntrada: null,
  idExpedicao: 0,
  blocos: [],
};

describe('PedidoCardItem', () => {
  let component: PedidoCardItem;
  let fixture: ComponentFixture<PedidoCardItem>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PedidoCardItem],
    }).compileComponents();

    fixture = TestBed.createComponent(PedidoCardItem);
    fixture.componentRef.setInput('pedido', mockPedido);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
