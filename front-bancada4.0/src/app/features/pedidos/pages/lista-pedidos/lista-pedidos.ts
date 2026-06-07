import { Component, signal, computed, inject } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Navbar } from '../../../../layout/navbar/navbar';
import { Footer } from '../../../../layout/footer/footer';
import { ButtonComponent } from '../../../../shared/components/button/button';
import { InputFieldComponent } from '../../../../shared/components/input-field/input-field';
import { PedidoTable } from '../../components/pedido-table/pedido-table';
import { Pedido } from '../../../../core/models/pedido';
import { StatusPedido } from '../../../../core/models/enums/statuspedido';
import { StatusCardPedido } from '../../../../shared/components/status-card-pedido/status-card-pedido';

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
  ],
  templateUrl: './lista-pedidos.html',
})
export class ListaPedidos {
  private router = inject(Router);

  readonly pedidos       = signal<Pedido[]>([]);
  readonly filtroAtual   = signal<Filtro>('TODOS');
  readonly pedidoDetalhe = signal<Pedido | null>(null);
  readonly pedidoExcluir = signal<number | null>(null);

  readonly buscaControl = new FormControl('');

  readonly pedidosFiltrados = computed(() => {
    const filtro = this.filtroAtual();
    const busca  = (this.buscaControl.value ?? '').toLowerCase().trim();

    return this.pedidos()
      .filter(p => {
        if (filtro === 'PENDENTE')  return p.status === StatusPedido.PENDENTE;
        if (filtro === 'PRODUCAO')  return p.status === StatusPedido.PRODUCAO;
        if (filtro === 'CONCLUIDO') return p.status === StatusPedido.CONCLUIDO;
        return true;
      })
      .filter(p =>
        !busca ||
        p.codPedido.toLowerCase().includes(busca) ||
        String(p.id).includes(busca)
      );
  });

  readonly totalPedidos  = computed(() => this.pedidos().length);
  readonly qtdPendentes  = computed(() => this.pedidos().filter(p => p.status === StatusPedido.PENDENTE).length);
  readonly qtdProducao   = computed(() => this.pedidos().filter(p => p.status === StatusPedido.PRODUCAO).length);
  readonly qtdConcluidos = computed(() => this.pedidos().filter(p => p.status === StatusPedido.CONCLUIDO).length);

  setFiltro(filtro: Filtro): void {
    this.filtroAtual.set(filtro);
    console.log(this.filtroAtual())
  }

  abrirDetalhe(pedido: Pedido): void {
    this.pedidoDetalhe.set(pedido);
  }

  abrirConfirmacaoExclusao(id: number): void {
    this.pedidoExcluir.set(id);
  }

}