import { Component, input, output, computed } from '@angular/core';
import { Pedido } from '../../../../core/models/pedido.model';
import { StatusPedido } from '../../../../core/models/enums/statuspedido.enum';
import { TipoPedido } from '../../../../core/models/enums/tipopedido.enum';
import { CorTampa } from '../../../../core/models/enums/cortampa.enum';
import { ButtonComponent } from '../../../../shared/components/button/button.component';

@Component({
  selector: 'tr[app-pedido-row]',
  imports: [ButtonComponent],
  templateUrl: './pedido-row.component.html',
})
export class PedidoRow {
  readonly pedido = input.required<Pedido>();

  readonly onVerDetalhe = output<Pedido>();
  readonly onExcluir    = output<number>();

  readonly statusLabel: Record<StatusPedido, string> = {
    [StatusPedido.PENDENTE]:  'Pendente',
    [StatusPedido.PRODUCAO]:  'Em Produção',
    [StatusPedido.CONCLUIDO]: 'Concluído',
  };

  readonly statusBadgeClass: Record<StatusPedido, string> = {
    [StatusPedido.PENDENTE]:  'badge-status badge-pendente',
    [StatusPedido.PRODUCAO]:  'badge-status badge-producao',
    [StatusPedido.CONCLUIDO]: 'badge-status badge-concluido',
  };

  readonly tipoLabel: Record<TipoPedido, string> = {
    [TipoPedido.SIMPLES]: 'Simples',
    [TipoPedido.DUPLO]:   'Duplo',
    [TipoPedido.TRIPLO]:  'Triplo',
  };

  readonly tampaLabel: Record<CorTampa, string> = {
    [CorTampa.PRETO]:    'Preto',
    [CorTampa.VERMELHO]: 'Vermelho',
    [CorTampa.AZUL]:     'Azul',
  };

  readonly tampaCssClass: Record<CorTampa, string> = {
    [CorTampa.PRETO]:    'color-swatch sw-preto',
    [CorTampa.VERMELHO]: 'color-swatch sw-vermelho',
    [CorTampa.AZUL]:     'color-swatch sw-azul',
  };

  readonly totalBlocos = computed(() => {
    const b = this.pedido().blocos;
    return Array.isArray(b) ? b.length : b ? 1 : 0;
  });

  verDetalhe(): void {
    this.onVerDetalhe.emit(this.pedido());
  }

  excluir(): void {
    this.onExcluir.emit(this.pedido().id);
  }
}