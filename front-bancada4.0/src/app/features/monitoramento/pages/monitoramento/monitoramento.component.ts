import { Component, computed, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { Navbar } from '../../../../layout/navbar/navbar.component';
import { Footer } from '../../../../layout/footer/footer.component';
import { ToastNotifications } from '../../../../shared/components/toast-notifications/toast-notifications.component';
import { ButtonComponent } from '../../../../shared/components/button/button.component';
import { EstacaoCardComponent } from '../../components/estacao-card/estacao-card.component';
import { BancadaOverviewComponent } from '../../components/bancada-overview/bancada-overview.component';
import { PedidoTimerComponent } from '../../components/pedido-timer/pedido-timer.component';
import { UltimoPedidoModalComponent } from '../../components/ultimo-pedido-modal/ultimo-pedido-modal.component';
import { MonitoramentoService } from '../../../../core/service/monitoramento.service';
import { Estacao } from '../../../../core/models/enums/estacao.enum';
import { StatusEstacao } from '../../../../core/models/enums/statusestacao.enum';
import { MonitoramentoSnapshot, UltimoPedidoResumo } from '../../../../core/models/monitoramento.model';

const MOCK_ENABLED = true;

const MOCK_SNAPSHOT: MonitoramentoSnapshot = {
  codPedidoAtual: 1042,
  inicioPedido: new Date(Date.now() - 23 * 60 * 1000).toISOString(),
  estacoes: [
    { estacao: Estacao.ESTOQUE, status: StatusEstacao.FINALIZADO, atualizadoEm: new Date().toISOString() },
    { estacao: Estacao.PROCESSO, status: StatusEstacao.OCUPADO, atualizadoEm: new Date().toISOString() },
    { estacao: Estacao.MONTAGEM, status: StatusEstacao.START, atualizadoEm: new Date().toISOString() },
    { estacao: Estacao.EXPEDICAO, status: StatusEstacao.START, atualizadoEm: new Date().toISOString() },
  ],
};

const MOCK_ULTIMO_PEDIDO: UltimoPedidoResumo = {
  codPedido: 1041,
  tempoTotalSegundos: 5415,
  horarioInicio: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString(),
  horarioFim: new Date(Date.now() - 60 * 60 * 1000).toISOString(),
  statusFinalEstacoes: [
    { estacao: Estacao.ESTOQUE, status: StatusEstacao.FINALIZADO, atualizadoEm: new Date().toISOString() },
    { estacao: Estacao.PROCESSO, status: StatusEstacao.FINALIZADO, atualizadoEm: new Date().toISOString() },
    { estacao: Estacao.MONTAGEM, status: StatusEstacao.FINALIZADO, atualizadoEm: new Date().toISOString() },
    { estacao: Estacao.EXPEDICAO, status: StatusEstacao.FINALIZADO, atualizadoEm: new Date().toISOString() },
  ],
};

const ESTACOES_PADRAO: Estacao[] = [Estacao.ESTOQUE, Estacao.PROCESSO, Estacao.MONTAGEM, Estacao.EXPEDICAO];

@Component({
  selector: 'app-monitoramento',
  imports: [
    Navbar,
    Footer,
    ToastNotifications,
    ButtonComponent,
    BancadaOverviewComponent,
    EstacaoCardComponent,
    PedidoTimerComponent,
    UltimoPedidoModalComponent,
  ],
  templateUrl: './monitoramento.component.html',
})
export class Monitoramento implements OnInit, OnDestroy {
  private readonly monitoramentoService = inject(MonitoramentoService);

  readonly connectionStatus = this.monitoramentoService.connectionStatus;

  readonly snapshot = computed<MonitoramentoSnapshot | null>(() => {
    const snapshot = this.monitoramentoService.snapshot();
    if (snapshot) return snapshot;
    return MOCK_ENABLED && this.connectionStatus() === 'error' ? MOCK_SNAPSHOT : null;
  });

  readonly codPedidoAtual = computed(() => this.snapshot()?.codPedidoAtual ?? null);
  readonly inicioPedido   = computed(() => this.snapshot()?.inicioPedido ?? null);

  readonly estacoes = computed(() => {
    const estacoesSnapshot = this.snapshot()?.estacoes;
    if (estacoesSnapshot) return estacoesSnapshot;
    return ESTACOES_PADRAO.map((estacao) => ({ estacao, status: null }));
  });

  readonly ultimoPedidoModalOpen = signal(false);
  readonly ultimoPedido          = signal<UltimoPedidoResumo | null>(null);
  readonly ultimoPedidoLoading   = signal(false);
  readonly ultimoPedidoError     = signal(false);

  ngOnInit(): void {
    this.monitoramentoService.connect();
  }

  ngOnDestroy(): void {
    this.monitoramentoService.disconnect();
  }

  abrirUltimoPedido(): void {
    this.ultimoPedidoModalOpen.set(true);
    this.ultimoPedidoLoading.set(true);
    this.ultimoPedidoError.set(false);
    this.ultimoPedido.set(null);

    this.monitoramentoService.getUltimoPedidoResumo().subscribe({
      next: (resumo) => {
        this.ultimoPedidoLoading.set(false);
        this.ultimoPedido.set(resumo);
      },
      error: () => {
        this.ultimoPedidoLoading.set(false);

        if (MOCK_ENABLED) {
          this.ultimoPedido.set(MOCK_ULTIMO_PEDIDO);
          return;
        }

        this.ultimoPedidoError.set(true);
      },
    });
  }

  fecharUltimoPedido(): void {
    this.ultimoPedidoModalOpen.set(false);
  }
}
