import { Component, input, output } from '@angular/core';
import { PedidoRow } from '../pedido-row/pedido-row.component';
import { PedidoCardItem } from '../pedido-card-item/pedido-card-item.component';
import { Pedido } from '../../../../core/models/pedido.model';
import { TableEmptyStateComponent } from "../../../../shared/components/table-empty-state/table-empty-state.component";

@Component({
  selector: 'app-pedido-table',
  imports: [PedidoRow, PedidoCardItem, TableEmptyStateComponent],
  templateUrl: './pedido-table.component.html',
})
export class PedidoTable {
  readonly pedidos    = input.required<Pedido[]>();
  readonly verDetalhe = output<Pedido>();
  readonly excluir    = output<number>();

  protected readonly colunas = [
    { key: 'id',             label: 'ID',                 align: 'left'  },
    { key: 'ordemProducao',  label: 'Ordem de Produção',  align: 'left'  },
    { key: 'tipo',           label: 'Tipo',               align: 'left'  },
    { key: 'tampa',          label: 'Tampa',              align: 'left'  },
    { key: 'blocos',         label: 'Blocos',             align: 'left'  },
    { key: 'status',         label: 'Status',             align: 'left'  },
    { key: 'data',           label: 'Data',               align: 'left'  },
    { key: 'acoes',          label: 'Ações',              align: 'right' },
  ] as const;
}