import { Component, computed, input } from '@angular/core';
import { Estacao } from '../../../../core/models/enums/estacao.enum';
import { StatusEstacao } from '../../../../core/models/enums/statusestacao.enum';
import { EstacaoStatusBadgeComponent } from '../../../../shared/components/estacao-status-badge/estacao-status-badge.component';

const ESTACAO_LABEL: Record<Estacao, string> = {
  [Estacao.ESTOQUE]: 'Estoque',
  [Estacao.PROCESSO]: 'Processo',
  [Estacao.MONTAGEM]: 'Montagem',
  [Estacao.EXPEDICAO]: 'Expedição',
};

const ESTACAO_ICON: Record<Estacao, string> = {
  [Estacao.ESTOQUE]: 'fa-solid fa-warehouse',
  [Estacao.PROCESSO]: 'fa-solid fa-gears',
  [Estacao.MONTAGEM]: 'fa-solid fa-screwdriver-wrench',
  [Estacao.EXPEDICAO]: 'fa-solid fa-truck-fast',
};

@Component({
  selector: 'app-estacao-card',
  imports: [EstacaoStatusBadgeComponent],
  template: `
    <div class="bg-bg-card border border-border-subtle rounded-smart p-4 flex flex-col gap-3
                 transition-all duration-300 hover:border-border-mid hover:shadow-card">
      <div class="flex items-center gap-3">
        <div class="w-10.5 h-10.5 rounded-lg flex items-center justify-center text-lg shrink-0
                     bg-slate-500/15 text-slate-300">
          <i [class]="icon()" aria-hidden="true"></i>
        </div>
        <div class="font-heading text-base font-bold leading-none text-text-primary">
          {{ label() }}
        </div>
      </div>

      @if (status()) {
        <app-estacao-status-badge [status]="status()!" />
      } @else {
        <div class="inline-flex items-center gap-2 px-3 py-1.5 bg-card border border-line rounded">
          <span class="font-mono-tech text-[0.7rem] tracking-[0.12em] text-text-muted">
            SEM DADOS
          </span>
        </div>
      }
    </div>
  `,
})
export class EstacaoCardComponent {
  readonly estacao = input.required<Estacao>();
  readonly status  = input<StatusEstacao | null>(null);

  readonly label = computed(() => ESTACAO_LABEL[this.estacao()]);
  readonly icon  = computed(() => ESTACAO_ICON[this.estacao()]);
}
