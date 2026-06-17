import { Component, computed, input } from '@angular/core';
import { StatusEstacao } from '../../../core/models/enums/statusestacao.enum';

interface StatusEstacaoConfig {
  readonly defaultLabel: string;
  readonly dot:  string;
  readonly ring: string;
  readonly text: string;
  readonly glow: string;
}

const STATUS_ESTACAO_CONFIG: Record<StatusEstacao, StatusEstacaoConfig> = {
  [StatusEstacao.START]: {
    defaultLabel: 'AGUARDANDO INÍCIO',
    dot:  'bg-slate-400',
    ring: 'bg-slate-400/25',
    text: 'text-slate-400',
    glow: '0 0 6px rgba(148,163,184,0.7)',
  },
  [StatusEstacao.OCUPADO]: {
    defaultLabel: 'EM OPERAÇÃO',
    dot:  'bg-blue-400',
    ring: 'bg-blue-400/25',
    text: 'text-blue-400',
    glow: '0 0 6px rgba(96,165,250,0.7)',
  },
  [StatusEstacao.CANCELADO]: {
    defaultLabel: 'CANCELADO',
    dot:  'bg-red-400',
    ring: 'bg-red-400/25',
    text: 'text-red-400',
    glow: '0 0 6px rgba(248,113,113,0.7)',
  },
  [StatusEstacao.FINALIZADO]: {
    defaultLabel: 'FINALIZADO',
    dot:  'bg-emerald-400',
    ring: 'bg-emerald-400/25',
    text: 'text-emerald-400',
    glow: '0 0 6px rgba(52,211,153,0.7)',
  },
};

@Component({
  selector: 'app-estacao-status-badge',
  template: `
    <div
      class="inline-flex items-center gap-2 px-3 py-1.5 bg-card border border-line rounded"
      role="status"
      [attr.aria-label]="ariaLabel()"
    >
      <span class="relative flex items-center justify-center w-3.5 h-3.5 shrink-0" aria-hidden="true">
        <span
          class="absolute w-full h-full rounded-full animate-pulse-ring"
          [class]="cfg().ring"
        ></span>
        <span
          class="relative z-10 w-2 h-2 rounded-full"
          [class]="cfg().dot"
          [style.box-shadow]="cfg().glow"
        ></span>
      </span>

      <span
        class="font-mono-tech text-[0.7rem] tracking-[0.12em]"
        [class]="cfg().text"
      >
        {{ resolvedLabel() }}
      </span>
    </div>
  `,
})
export class EstacaoStatusBadgeComponent {
  readonly status = input<StatusEstacao>(StatusEstacao.START);
  readonly label  = input('');

  readonly cfg           = computed(() => STATUS_ESTACAO_CONFIG[this.status()]);
  readonly resolvedLabel = computed(() => this.label() || this.cfg().defaultLabel);
  readonly ariaLabel     = computed(() => `Status da estação: ${this.resolvedLabel().toLowerCase()}`);
}
