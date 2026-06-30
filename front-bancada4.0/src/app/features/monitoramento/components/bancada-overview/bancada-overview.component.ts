import { Component, computed, input } from '@angular/core';
import { NgOptimizedImage } from '@angular/common';
import { Estacao } from '../../../../core/models/enums/estacao.enum';
import { StatusEstacao } from '../../../../core/models/enums/statusestacao.enum';

export interface BancadaOverviewItem {
  estacao: Estacao;
  status: StatusEstacao | null;
}

interface BancadaOverlay {
  estacao: Estacao;
  barSrc: string | null;
  iconSrc: string | null;
}

const BAR_PREFIX: Record<Estacao, string> = {
  [Estacao.ESTOQUE]: 'Est',
  [Estacao.PROCESSO]: 'Pro',
  [Estacao.MONTAGEM]: 'Mon',
  [Estacao.EXPEDICAO]: 'Exp',
};

const ICON_PREFIX: Record<Estacao, string> = {
  [Estacao.ESTOQUE]: 'Estoque',
  [Estacao.PROCESSO]: 'Processo',
  [Estacao.MONTAGEM]: 'Montagem',
  [Estacao.EXPEDICAO]: 'Expedicao',
};

const BAR_SUFFIX: Record<StatusEstacao, string> = {
  [StatusEstacao.START]: 'on',
  [StatusEstacao.OCUPADO]: 'pause',
  [StatusEstacao.FINALIZADO]: 'on',
  [StatusEstacao.CANCELADO]: 'off',
};

const ICON_SUFFIX: Record<StatusEstacao, string | null> = {
  [StatusEstacao.START]: '0',
  [StatusEstacao.OCUPADO]: '1',
  [StatusEstacao.FINALIZADO]: '2',
  [StatusEstacao.CANCELADO]: null,
};

@Component({
  selector: 'app-bancada-overview',
  imports: [NgOptimizedImage],
  template: `
    <div class="relative w-full" style="aspect-ratio: 703 / 355">
      <img
        ngSrc="assets/bancada/Smart40.png"
        fill
        priority
        class="object-contain"
        alt="Visão geral da bancada Smart 4.0"
      />

      @for (overlay of overlays(); track overlay.estacao) {
        @if (overlay.barSrc) {
          <img [ngSrc]="overlay.barSrc" fill class="object-contain" alt="" />
        }
        @if (overlay.iconSrc) {
          <img [ngSrc]="overlay.iconSrc" fill class="object-contain" alt="" />
        }
      }
    </div>
  `,
})
export class BancadaOverviewComponent {
  readonly estacoes = input<BancadaOverviewItem[]>([]);

  readonly overlays = computed<BancadaOverlay[]>(() =>
    this.estacoes().map(({ estacao, status }) => ({
      estacao,
      barSrc: status ? `assets/bancada/Smart40-${BAR_PREFIX[estacao]}_${BAR_SUFFIX[status]}.png` : null,
      iconSrc: status && ICON_SUFFIX[status]
        ? `assets/bancada/Smart40_${ICON_PREFIX[estacao]}_${ICON_SUFFIX[status]}.png`
        : null,
    })),
  );
}
