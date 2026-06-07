import { Component, input } from '@angular/core';

@Component({
  selector: 'app-status-card-pedido',
  imports: [],
  template: `
    <div class="stat-card">
      <div class="stat-icon" [class]="iconColorClass[type()]">
        <i [class]="iconClass[type()]" aria-hidden="true"></i>
      </div>
      <div>
        <div class="stat-value">{{ total() }}</div>
        <div class="stat-label">{{ label[type()] }}</div>
      </div>
    </div>
  `,
})
export class StatusCardPedido {
  readonly type  = input.required<'total' | 'pendente' | 'producao' | 'concluido'>();
  readonly total = input.required<number>();

  readonly iconClass: Record<string, string> = {
    total:     'fa-solid fa-list',
    pendente:  'fa-solid fa-clock',
    producao:  'fa-solid fa-gears',
    concluido: 'fa-solid fa-check',
  };

  readonly iconColorClass: Record<string, string> = {
    total:     'gray',
    pendente:  'amber',
    producao:  'blue',
    concluido: 'green',
  };

  readonly label: Record<string, string> = {
    total:     'Total',
    pendente:  'Pendente',
    producao:  'Em Produção',
    concluido: 'Concluído',
  };
}