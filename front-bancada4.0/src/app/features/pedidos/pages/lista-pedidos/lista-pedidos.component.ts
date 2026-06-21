import { Component, signal, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Navbar } from '../../../../layout/navbar/navbar.component';
import { Footer } from '../../../../layout/footer/footer.component';
import { ButtonComponent } from '../../../../shared/components/button/button.component';
import { PedidoTable } from '../../components/pedido-table/pedido-table.component';
import { Pedido } from '../../../../core/models/pedido.model';
import { StatusPedido } from '../../../../core/models/enums/statuspedido.enum';
import { StatusCardPedido } from '../../../../shared/components/status-card-pedido/status-card-pedido.component';
import { PaginationComponent } from '../../../../shared/components/pagination/pagination.component';
import { PedidoService, PedidoFiltro } from '../../../../core/service/pedido.service';
import { PedidoContagens } from '../../../../core/models/pedido-contagens.model';
import { PedidoDetalheModalComponent } from "../../components/pedido-detalhe-modal/pedido-detalhe-modal.component";
import { ModalComponent } from '../../../../shared/components/modal/modal.component';
import { ToastNotifications } from '../../../../shared/components/toast-notifications/toast-notifications.component';

const PAGE_SIZE = 10;

@Component({
  selector: 'app-lista-pedidos',
  imports: [
    Navbar,
    Footer,
    ButtonComponent,
    StatusCardPedido,
    PedidoTable,
    PaginationComponent,
    PedidoDetalheModalComponent,
    ModalComponent,
    ToastNotifications
  ],
  templateUrl: './lista-pedidos.component.html',
})
export class ListaPedidos {

  private readonly pedidoService = inject(PedidoService);
  private router = inject(Router);

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
    this.pedidoService.getPedidosPaginado(this.filtroAtual(), this.pageIndex(), this.pageSize).subscribe({
      next: (page) => {
        this.pedidos.set(page.content);
        this.totalPages.set(page.totalPages);
        this.totalElements.set(page.totalElements);
        this.first.set(page.first);
        this.last.set(page.last);
        this.loading.set(false);
      },
      error: (err) => {
        console.log(err);
        this.loading.set(false);
      },
    });
  }

  carregarContagens(): void {
    this.pedidoService.getContagens().subscribe({
      next: (contagens) => this.contagens.set(contagens),
      error: (err) => console.log(err),
    });
  }

  onRetirar(pedido: Pedido) {
    console.log("Retirando pedido: "+pedido.codPedido)
  }

  onReiniciar(pedido: Pedido) {
    this.pedidoService.reiniciarPedido(pedido.id).subscribe({
      next: () => {
        this.carregarPagina();
        this.carregarContagens();
        this.fecharDetalhe();
        this.saveSuccess.set(true);
        setTimeout(() => this.saveSuccess.set(false), 2000);
      },
      error: (err) => {
        console.log(err);
        this.saveError.set(true);
        setTimeout(() => this.saveError.set(false), 3000);
      },
    });
  }

  onSalvarEdicao(pedido: Pedido) {
    this.pedidoService.patchPedido(pedido.id, pedido).subscribe({
      next: () => {
        this.carregarPagina();
        this.carregarContagens();
        this.fecharDetalhe();
        this.saveSuccess.set(true);
        setTimeout(() => this.saveSuccess.set(false), 2000);
      },
      error: (err) => {
        console.log(err);
        this.saveError.set(true);
        setTimeout(() => this.saveError.set(false), 3000);
      },
    });
  }

  onEnviarProducao(pedido: Pedido) {
    this.pedidoProducao.set(pedido);
    this.confirmarEnvioProducao();
  }

  fecharDetalhe() {
    this.pedidoSelecionado.set(null);
  }

  cancelarEnvioProducao() {
    this.pedidoProducao.set(null);
  }

  confirmarEnvioProducao() {
    const pedido = this.pedidoProducao();

    if (!pedido) {
      return;
    }

   this.pedidoService.postEnviarPedido(pedido).subscribe({
    next: (pedido) =>{
      console.log(pedido);
      this.carregarPagina();
      this.carregarContagens();
    },
    error(err) {
      console.log(err)
    },
   });

    this.cancelarEnvioProducao();
    this.fecharDetalhe();
  }
}
