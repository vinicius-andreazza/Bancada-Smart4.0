import { Component, computed, input, output } from '@angular/core';
import { Pedido } from '../../../../core/models/pedido.model';
import { ButtonComponent } from '../../../../shared/components/button/button.component';
import {
  STATUS_LABEL,
  STATUS_BADGE_CLASS,
  TIPO_LABEL,
  TAMPA_LABEL,
  TAMPA_CSS_CLASS,
  getTotalBlocos,
} from '../pedido-row/pedido-labels';

/**
 * Versão em card de um pedido, usada na lista de pedidos em telas pequenas (`md:hidden`),
 * onde a tabela de 8 colunas não cabe. Espelha os dados/ações da `pedido-row`.
 */
@Component({
  selector: 'app-pedido-card-item',
  imports: [ButtonComponent],
  templateUrl: './pedido-card-item.component.html',
})
export class PedidoCardItem {
  readonly pedido = input.required<Pedido>();

  readonly verDetalhe = output<Pedido>();
  readonly excluir    = output<number>();

  readonly statusLabel      = STATUS_LABEL;
  readonly statusBadgeClass = STATUS_BADGE_CLASS;
  readonly tipoLabel        = TIPO_LABEL;
  readonly tampaLabel       = TAMPA_LABEL;
  readonly tampaCssClass    = TAMPA_CSS_CLASS;

  readonly totalBlocos = computed(() => getTotalBlocos(this.pedido()));
}
