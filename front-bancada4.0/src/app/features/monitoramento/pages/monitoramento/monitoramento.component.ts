import { Component, computed, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { Navbar } from '../../../../layout/navbar/navbar.component';
import { Footer } from '../../../../layout/footer/footer.component';
import { ToastNotifications } from '../../../../shared/components/toast-notifications/toast-notifications.component';
import { ButtonComponent } from '../../../../shared/components/button/button.component';
import { EstacaoCardComponent } from '../../components/estacao-card/estacao-card.component';
import { BancadaOverviewComponent } from '../../components/bancada-overview/bancada-overview.component';
import { PedidoTimerComponent } from '../../components/pedido-timer/pedido-timer.component';
import { UltimoPedidoModalComponent } from '../../components/ultimo-pedido-modal/ultimo-pedido-modal.component';
import { EstoquePanelClpComponent } from '../../components/estoque-panel-clp/estoque-panel-clp.component';
import { ExpedicaoPanelClpComponent } from '../../components/expedicao-panel-clp/expedicao-panel-clp.component';
import { MonitoramentoService } from '../../../../core/service/monitoramento.service';
import { PedidoService } from '../../../../core/service/pedido.service';
import { Estacao } from '../../../../core/models/enums/estacao.enum';
import { Pedido } from '../../../../core/models/pedido.model';

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
    EstoquePanelClpComponent,
    ExpedicaoPanelClpComponent,
  ],
  templateUrl: './monitoramento.component.html',
})
export class Monitoramento implements OnInit, OnDestroy {
  private readonly monitoramentoService = inject(MonitoramentoService);
  private readonly pedidoService = inject(PedidoService);

  readonly connectionStatus = this.monitoramentoService.connectionStatus;
  readonly snapshot = this.monitoramentoService.snapshot;

  readonly codPedidoAtual = computed(() => this.snapshot()?.codPedidoAtual ?? null);
  readonly inicioPedido   = computed(() => this.snapshot()?.inicioPedido ?? null);

  readonly estoquePositions   = computed(() => this.snapshot()?.estoque   ?? []);
  readonly expedicaoPositions = computed(() => this.snapshot()?.expedicao ?? []);

  readonly estacoes = computed(() => {
    const estacoesSnapshot = this.snapshot()?.estacoes;
    if (estacoesSnapshot) return estacoesSnapshot;
    return ESTACOES_PADRAO.map((estacao) => ({ estacao, status: null }));
  });

  readonly ultimoPedidoModalOpen = signal(false);
  readonly ultimoPedido          = signal<Pedido | null>(null);
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

    this.pedidoService.getUltimoPedido().subscribe({
      next: (pedido) => {
        this.ultimoPedidoLoading.set(false);
        this.ultimoPedido.set(pedido);
      },
      error: () => {
        this.ultimoPedidoLoading.set(false);
        this.ultimoPedidoError.set(true);
      },
    });
  }

  fecharUltimoPedido(): void {
    this.ultimoPedidoModalOpen.set(false);
  }
}
