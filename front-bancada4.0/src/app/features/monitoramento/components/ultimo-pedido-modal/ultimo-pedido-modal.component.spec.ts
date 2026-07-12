import { Component, input } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UltimoPedidoModalComponent } from './ultimo-pedido-modal.component';
import { Pedido3dPreviewComponent } from '../../../pedidos/components/pedido-3d-preview/pedido-3d-preview.component';
import { Pedido } from '../../../../core/models/pedido.model';
import { StatusPedido } from '../../../../core/models/enums/statuspedido.enum';
import { TipoPedido } from '../../../../core/models/enums/tipopedido.enum';
import { CorTampa } from '../../../../core/models/enums/cortampa.enum';

// Stub the 3D preview: the real one boots three.js/WebGL, which is unavailable in jsdom.
@Component({ selector: 'app-pedido-3d-preview', template: '' })
class Pedido3dPreviewStubComponent {
  readonly staticConfig = input<unknown>(null);
}

describe('UltimoPedidoModalComponent', () => {
  let component: UltimoPedidoModalComponent;
  let fixture: ComponentFixture<UltimoPedidoModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UltimoPedidoModalComponent],
    })
      .overrideComponent(UltimoPedidoModalComponent, {
        remove: { imports: [Pedido3dPreviewComponent] },
        add: { imports: [Pedido3dPreviewStubComponent] },
      })
      .compileComponents();

    fixture = TestBed.createComponent(UltimoPedidoModalComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('shows loading state', () => {
    fixture.componentRef.setInput('loading', true);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Carregando');
  });

  it('shows empty state when there is no pedido', () => {
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Nenhum pedido concluído encontrado');
  });

  it('shows the pedido data when provided', () => {
    const pedido: Pedido = {
      id: 123,
      codPedido: 456,
      dataCriacao: '2026-06-11T08:00:00.000Z',
      status: StatusPedido.CONCLUIDO,
      tipoPedido: TipoPedido.SIMPLES,
      corTampa: CorTampa.PRETO,
      dataInicio: null,
      dataEntrada: null,
      idExpedicao: 0,
      blocos: [],
    };

    fixture.componentRef.setInput('resumo', pedido);
    fixture.detectChanges();

    const el: HTMLElement = fixture.nativeElement;
    expect(el.textContent).toContain('#123');
    expect(el.textContent).toContain('Concluído');
    expect(el.querySelector('app-pedido-3d-preview')).toBeTruthy();
  });
});
