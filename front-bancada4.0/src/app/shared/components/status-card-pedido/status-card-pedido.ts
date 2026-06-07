import { Component, computed, input } from '@angular/core';
import { StatusPedido } from '../../../core/models/enums/statuspedido';

export type StatusCardType = StatusPedido | 'TOTAL';

const ICON_CLASS: Record<StatusCardType, string> = {
  TOTAL:                   'fa-solid fa-list',
  [StatusPedido.PENDENTE]: 'fa-solid fa-clock',
  [StatusPedido.PRODUCAO]: 'fa-solid fa-gears',
  [StatusPedido.CONCLUIDO]:'fa-solid fa-check',
};

const ICON_COLOR_CLASS: Record<StatusCardType, string> = {
  TOTAL:                   'gray',
  [StatusPedido.PENDENTE]: 'amber',
  [StatusPedido.PRODUCAO]: 'blue',
  [StatusPedido.CONCLUIDO]:'green',
};

const LABEL: Record<StatusCardType, string> = {
  TOTAL:                   'Total',
  [StatusPedido.PENDENTE]: 'Pendente',
  [StatusPedido.PRODUCAO]: 'Em Produção',
  [StatusPedido.CONCLUIDO]:'Concluído',
};

@Component({
  selector: 'app-status-card-pedido',
  imports: [],
  template: `
    <div class="stat-card" [attr.aria-label]="labelText() + ': ' + total()">
      <div class="stat-icon" [class]="color()">
        <i [class]="icon()" aria-hidden="true"></i>
      </div>
      <div>
        <div class="stat-value">{{ total() }}</div>
        <div class="stat-label">{{ labelText() }}</div>
      </div>
    </div>
  `,
})
export class StatusCardPedido {
  readonly type  = input.required<StatusCardType>();
  readonly total = input.required<number>();

  protected readonly icon      = computed(() => ICON_CLASS[this.type()]);
  protected readonly color     = computed(() => ICON_COLOR_CLASS[this.type()]);
  protected readonly labelText = computed(() => LABEL[this.type()]);
}