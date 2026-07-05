import { Component, DestroyRef, OnInit, signal, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Navbar } from '../../../../layout/navbar/navbar.component';
import { Footer } from '../../../../layout/footer/footer.component';
import { PedidoTable } from '../../components/pedido-table/pedido-table.component';
import { Pedido } from '../../../../core/models/pedido.model';
import { StatusPedido } from '../../../../core/models/enums/statuspedido.enum';
import { StatusCardPedido } from '../../../../shared/components/status-card-pedido/status-card-pedido.component';
import { PaginationComponent } from '../../../../shared/components/pagination/pagination.component';
import { PedidoService, PedidoFiltro } from '../../../../core/service/pedido.service';
import { PedidoContagens } from '../../../../core/models/pedido-contagens.model';
import { PedidoDetalheModalComponent } from "../../components/pedido-detalhe-modal/pedido-detalhe-modal.component";
import { ToastNotifications } from '../../../../shared/components/toast-notifications/toast-notifications.component';
import { PedidoFiltrosComponent } from '../../components/pedido-filtros/pedido-filtros.component';
import { flashSignal } from '../../../../shared/utils/flash-signal';

const PAGE_SIZE = 10;

@Component({
  selector: 'app-lista-pedidos',
  imports: [
    Navbar,
    Footer,
    StatusCardPedido,
    PedidoTable,
    PaginationComponent,
    PedidoDetalheModalComponent,
    ToastNotifications,
    PedidoFiltrosComponent,
  ],
  templateUrl: './lista-pedidos.component.html',
})
export class ListaPedidos implements OnInit {

  private readonly pedidoService = inject(PedidoService);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly StatusPedido = StatusPedido;
  protected readonly pageSize = PAGE_SIZE;

  readonly pedidos = signal<Pedido[]>([]);
  readonly filtroAtual = signal<PedidoFiltro>('TODOS');
  readonly pedidoExcluir = signal<number | null>(null);

  readonly pageIndex = signal(0);
  readonly totalPages = signal(0);
  readonly totalElements = signal(0);
  readonly first = signal(true);
  readonly last = signal(true);
  readonly loading = signal(false);

  readonly contagens = signal<PedidoContagens | null>(null);

  readonly pedidoSelecionado = signal<Pedido | null>(null);
  readonly pedidoProducao = signal<Pedido | null>(null);

  readonly saveSuccess = signal(false);
  readonly saveError = signal(false);

  ngOnInit(): void {
    this.carregarPagina();
    this.carregarContagens();
  }

  setFiltro(filtro: PedidoFiltro): void {
    this.filtroAtual.set(filtro);
    this.pageIndex.set(0);
    this.carregarPagina();
  }

  irParaPagina(index: number): void {
    this.pageIndex.set(index);
    this.carregarPagina();
  }

  abrirDetalhe(pedido: Pedido): void {
    this.pedidoSelecionado.set(pedido);
  }

  abrirConfirmacaoExclusao(id: number): void {
    this.pedidoExcluir.set(id);
  }

  carregarPagina(): void {
    this.loading.set(true);
    this.pedidoService.getPedidosPaginado(this.filtroAtual(), this.pageIndex(), this.pageSize)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (page) => {
          this.pedidos.set(page.content);
          this.totalPages.set(page.totalPages);
          this.totalElements.set(page.totalElements);
          this.first.set(page.first);
          this.last.set(page.last);
          this.loading.set(false);
        },
        error: () => this.loading.set(false),
      });
  }

  carregarContagens(): void {
    this.pedidoService.getContagens()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((contagens) => this.contagens.set(contagens));
  }

  onRefazer(pedido: Pedido): void {
    this.pedidoService.remakePedido(pedido.codPedido)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.carregarPagina();
          this.carregarContagens();
          this.fecharDetalhe();
          flashSignal(this.saveSuccess, 2000);
        },
        error: () => flashSignal(this.saveError, 3000),
      });
  }

  onReiniciar(pedido: Pedido): void {
    this.pedidoService.reiniciarPedido(pedido.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.carregarPagina();
          this.carregarContagens();
          this.fecharDetalhe();
          flashSignal(this.saveSuccess, 2000);
        },
        error: () => flashSignal(this.saveError, 3000),
      });
  }

  onSalvarEdicao(pedido: Pedido): void {
    console.log(pedido)
    this.pedidoService.patchPedido(pedido.id, pedido)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.carregarPagina();
          this.carregarContagens();
          this.fecharDetalhe();
          flashSignal(this.saveSuccess, 2000);
        },
        error: () => flashSignal(this.saveError, 3000),
      });
  }

  onEnviarProducao(pedido: Pedido): void {
    this.pedidoProducao.set(pedido);
    this.confirmarEnvioProducao();
  }

  fecharDetalhe(): void {
    this.pedidoSelecionado.set(null);
  }

  cancelarEnvioProducao(): void {
    this.pedidoProducao.set(null);
  }

  confirmarEnvioProducao(): void {
    const pedido = this.pedidoProducao();

    if (!pedido) {
      return;
    }

    this.pedidoService.postEnviarPedido(pedido)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.carregarPagina();
          this.carregarContagens();
        },
      });

    this.cancelarEnvioProducao();
    this.fecharDetalhe();
  }
}
