import { Component, signal, computed, inject } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Navbar } from '../../../../layout/navbar/navbar.component';
import { Footer } from '../../../../layout/footer/footer.component';
import { ButtonComponent } from '../../../../shared/components/button/button.component';
import { InputFieldComponent } from '../../../../shared/components/input-field/input-field.component';
import { PedidoTable } from '../../components/pedido-table/pedido-table.component';
import { Pedido } from '../../../../core/models/pedido.model';
import { StatusPedido } from '../../../../core/models/enums/statuspedido.enum';
import { StatusCardPedido } from '../../../../shared/components/status-card-pedido/status-card-pedido.component';
import { PedidoService } from '../../../../core/service/pedido.service';
import { PedidoDetalheModalComponent } from "../../components/pedido-detalhe-modal/pedido-detalhe-modal.component";
import { ModalComponent } from '../../../../shared/components/modal/modal.component';
import { ToastNotifications } from '../../../../shared/components/toast-notifications/toast-notifications.component';

type Filtro = 'TODOS' | 'PENDENTE' | 'PRODUCAO' | 'CONCLUIDO';

@Component({
  selector: 'app-lista-pedidos',
  imports: [
    ReactiveFormsModule,
    Navbar,
    Footer,
    ButtonComponent,
    InputFieldComponent,
    StatusCardPedido,
    PedidoTable,
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

  readonly pedidos = signal<Pedido[]>([]);
  readonly filtroAtual = signal<Filtro>('TODOS');
  readonly pedidoExcluir = signal<number | null>(null);

  readonly buscaControl = new FormControl('');

  readonly pedidoSelecionado = signal<Pedido | null>(null);

  readonly pedidoProducao = signal<Pedido | null>(null);

  readonly saveSuccess = signal(false);
  readonly saveError = signal(false);

  readonly ipClpControl = new FormControl('');

  readonly pedidosFiltrados = computed(() => {
    const filtro = this.filtroAtual();
    const busca = this.buscaControl.value;

    return this.pedidos()
      .filter(p => {
        if (filtro === 'PENDENTE') return p.status === StatusPedido.PENDENTE;
        if (filtro === 'PRODUCAO') return p.status === StatusPedido.PRODUCAO;
        if (filtro === 'CONCLUIDO') return p.status === StatusPedido.CONCLUIDO;
        return true;
      })
  });

  readonly totalPedidos = computed(() => this.pedidos().length);
  readonly qtdPendentes = computed(() => this.pedidos().filter(p => p.status === StatusPedido.PENDENTE).length);
  readonly qtdProducao = computed(() => this.pedidos().filter(p => p.status === StatusPedido.PRODUCAO).length);
  readonly qtdConcluidos = computed(() => this.pedidos().filter(p => p.status === StatusPedido.CONCLUIDO).length);

  ngOnInit(): void {
    this.getPedidos();
  }

  setFiltro(filtro: Filtro): void {
    this.filtroAtual.set(filtro);
  }

  abrirDetalhe(pedido: Pedido): void {
    this.pedidoSelecionado.set(pedido);
  }

  abrirConfirmacaoExclusao(id: number): void {
    this.pedidoExcluir.set(id);
  }

  getPedidos() {
    this.pedidoService.getPedido().subscribe({
      next: (pedido: Pedido[]) => {
        this.pedidos.set(pedido);
      },
      error(err) {
        console.log(err)
      },
    })
  }

  onRetirar(pedido: Pedido) {
    console.log("Retirando pedido: "+pedido.codPedido)
  }

  onSalvarEdicao(pedido: Pedido) {
    this.pedidoService.patchPedido(pedido.id, pedido).subscribe({
      next: () => {
        this.getPedidos();
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
    },
    error(err) {
      console.log(err)
    },
   });

    this.cancelarEnvioProducao();
    this.fecharDetalhe();
  }
}