import { Component, input, output, computed } from '@angular/core';
import { Pedido } from '../../../../core/models/pedido.model';
import { ButtonComponent } from '../../../../shared/components/button/button.component';
import {
  STATUS_LABEL,
  STATUS_BADGE_CLASS,
  TIPO_LABEL,
  TAMPA_LABEL,
  TAMPA_CSS_CLASS,
  getTotalBlocos,
} from '../../shared/utils/pedido-labels';

@Component({
  selector: 'tr[app-pedido-row]',
  imports: [ButtonComponent],
  templateUrl: './pedido-row.component.html',
})
export class PedidoRow {
  readonly pedido = input.required<Pedido>();

  readonly onVerDetalhe = output<Pedido>();
  readonly onExcluir    = output<number>();

  readonly statusLabel      = STATUS_LABEL;
  readonly statusBadgeClass = STATUS_BADGE_CLASS;
  readonly tipoLabel        = TIPO_LABEL;
  readonly tampaLabel       = TAMPA_LABEL;
  readonly tampaCssClass    = TAMPA_CSS_CLASS;

  readonly dataCriacao = computed(() => this.pedido().dataCriacao)
  readonly totalBlocos = computed(() => getTotalBlocos(this.pedido()));

  verDetalhe(): void {
    this.onVerDetalhe.emit(this.pedido());
  }

  excluir(): void {
    this.onExcluir.emit(this.pedido().id);
  }
}
