import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { signal } from '@angular/core';

import { Monitoramento } from './monitoramento.component';
import { MonitoramentoService } from '../../../../core/service/monitoramento.service';
import { PedidoService } from '../../../../core/service/pedido.service';
import { Pedido } from '../../../../core/models/pedido.model';

describe('Monitoramento', () => {
  let component: Monitoramento;
  let fixture: ComponentFixture<Monitoramento>;
  let monitoramentoServiceMock: Partial<MonitoramentoService>;
  let pedidoServiceMock: Partial<PedidoService>;

  beforeEach(async () => {
    monitoramentoServiceMock = {
      snapshot: signal(null),
      connectionStatus: signal('error'),
      connect: vi.fn(),
      disconnect: vi.fn(),
    };

    pedidoServiceMock = {
      getUltimoPedido: () => of({ blocos: [] } as unknown as Pedido),
      getPedidosEmProducao: () => of([]),
    };

    await TestBed.configureTestingModule({
      imports: [Monitoramento],
      providers: [
        provideRouter([]),
        { provide: MonitoramentoService, useValue: monitoramentoServiceMock },
        { provide: PedidoService, useValue: pedidoServiceMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Monitoramento);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('renders the 4 estacao cards', () => {
    fixture.detectChanges();
    const el: HTMLElement = fixture.nativeElement;
    expect(el.querySelectorAll('app-estacao-card').length).toBe(4);
  });

  it('shows the connection error toast', () => {
    fixture.detectChanges();
    const el: HTMLElement = fixture.nativeElement;
    expect(el.textContent).toContain('Sem conexão com o sistema de monitoramento');
  });

  it('opens the último pedido modal', () => {
    fixture.detectChanges();
    component.abrirUltimoPedido();
    fixture.detectChanges();

    const el: HTMLElement = fixture.nativeElement;
    expect(el.querySelector('app-ultimo-pedido-modal')).toBeTruthy();
  });
});
