import { Component, input, output } from '@angular/core';
import { PedidoRow } from '../pedido-row/pedido-row';
import { Pedido } from '../../../../core/models/pedido';

@Component({
  selector: 'app-pedido-table',
  imports: [PedidoRow],
  templateUrl: './pedido-table.html',
})
export class PedidoTable {
  readonly pedidos    = input.required<Pedido[]>();
  readonly verDetalhe = output<Pedido>();
  readonly excluir    = output<number>();
}