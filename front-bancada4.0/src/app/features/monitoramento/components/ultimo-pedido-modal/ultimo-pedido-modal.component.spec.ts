import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UltimoPedidoModalComponent } from './ultimo-pedido-modal.component';
import { UltimoPedidoResumo } from '../../../../core/models/monitoramento.model';
import { Estacao } from '../../../../core/models/enums/estacao.enum';
import { StatusEstacao } from '../../../../core/models/enums/statusestacao.enum';

describe('UltimoPedidoModalComponent', () => {
  let component: UltimoPedidoModalComponent;
  let fixture: ComponentFixture<UltimoPedidoModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UltimoPedidoModalComponent],
    }).compileComponents();

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

  it('shows empty state when there is no resumo', () => {
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Nenhum pedido concluído encontrado');
  });

  it('shows the resumo data when provided', () => {
    const resumo: UltimoPedidoResumo = {
      codPedido: 123,
      tempoTotalSegundos: 3725,
      horarioInicio: '2026-06-11T08:00:00.000Z',
      horarioFim: '2026-06-11T09:02:05.000Z',
      statusFinalEstacoes: [
        { estacao: Estacao.ESTOQUE, status: StatusEstacao.FINALIZADO, atualizadoEm: '2026-06-11T09:00:00.000Z' },
        { estacao: Estacao.PROCESSO, status: StatusEstacao.FINALIZADO, atualizadoEm: '2026-06-11T09:00:00.000Z' },
        { estacao: Estacao.MONTAGEM, status: StatusEstacao.FINALIZADO, atualizadoEm: '2026-06-11T09:01:00.000Z' },
        { estacao: Estacao.EXPEDICAO, status: StatusEstacao.FINALIZADO, atualizadoEm: '2026-06-11T09:02:00.000Z' },
      ],
    };

    fixture.componentRef.setInput('resumo', resumo);
    fixture.detectChanges();

    const el: HTMLElement = fixture.nativeElement;
    expect(el.textContent).toContain('#123');
    expect(el.textContent).toContain('01:02:05');
    expect(el.querySelectorAll('app-estacao-status-badge').length).toBe(4);
  });
});
