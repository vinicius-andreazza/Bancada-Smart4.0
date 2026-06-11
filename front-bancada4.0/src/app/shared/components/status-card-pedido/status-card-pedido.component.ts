import { Component, computed, input } from '@angular/core';
import { StatusPedido } from '../../../core/models/enums/statuspedido.enum';

export type StatusCardType = StatusPedido | 'TOTAL';

const ICON_CLASS: Record<StatusCardType, string> = {
  TOTAL:                   'fa-solid fa-list',
  [StatusPedido.PENDENTE]: 'fa-solid fa-clock',
  [StatusPedido.PRODUCAO]: 'fa-solid fa-gears',
  [StatusPedido.CONCLUIDO]:'fa-solid fa-check',
};

const ICON_COLOR_CLASS: Record<StatusCardType, string> = {
  TOTAL:                   'bg-slate-500/15 text-slate-400',
  [StatusPedido.PENDENTE]: 'bg-amber-500/15 text-amber-400',
  [StatusPedido.PRODUCAO]: 'bg-blue-500/15 text-blue-400',
  [StatusPedido.CONCLUIDO]:'bg-emerald-500/15 text-emerald-400',
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
    <div class=" bg-bg-card border border-border-subtle rounded-smart p-4 flex items-center gap-4
           transition-all duration-300
           hover:border-border-mid hover:shadow-card" [attr.aria-label]="labelText() + ': ' + total()">
      <div class="w-10.5 h-10.5 rounded-lg flex items-center justify-center text-lg shrink-0" [class]="color()">
        <i [class]="icon()" aria-hidden="true"></i>
      </div>
      <div>
        <div class="font-heading text-[1.8rem] font-bold leading-none">{{ total() }}</div>
        <div class="text-[0.7rem] text-text-secondary font-mono tracking-[0.06em] uppercase mt-1">{{ labelText() }}</div>
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