import { Component, computed, input, output } from '@angular/core';
import { Estoque } from '../../../../../core/models/estoque.model';
import { CorBloco } from '../../../../../core/models/enums/corbloco.enum';

const CELL_COLOR_CLASS: Record<CorBloco, string> = {
  [CorBloco.VAZIO]:    'bg-slate-800/60 border-slate-700/50 text-slate-500',
  [CorBloco.PRETO]:    'bg-neutral-800  border-neutral-600  text-neutral-400',
  [CorBloco.VERMELHO]: 'bg-red-900/70   border-red-700/60   text-red-400',
  [CorBloco.AZUL]:     'bg-blue-900/70  border-blue-700/60  text-blue-400',
};

const COLOR_LABEL: Record<CorBloco, string> = {
  [CorBloco.VAZIO]:    'VAZIO',
  [CorBloco.PRETO]:    'PRETO',
  [CorBloco.VERMELHO]: 'VERMELHO',
  [CorBloco.AZUL]:     'AZUL',
};

@Component({
  selector: '[app-estoque-cell]',
  imports: [],
  template: `
    <span
      class="text-[0.6rem] sm:text-[0.7rem] font-mono font-bold leading-none opacity-60"
      aria-hidden="true"
    >
      {{ pos().posicao }}
    </span>

    @if (editMode() && changed()) {
      <span
        class="absolute top-1 right-1 w-1.5 h-1.5 rounded-full z-10
               bg-amber-400 shadow-[0_0_6px_rgba(251,191,36,0.8)]
               animate-[dotBlink_1.8s_ease_infinite]"
        aria-hidden="true"
        title="Alterado"
      ></span>
    }

    <div
      class="text-[0.55rem] sm:text-[0.65rem] font-mono font-semibold tracking-wide uppercase leading-none opacity-70 max-w-full truncate text-center"
      aria-hidden="true"
    >
      {{ colorLabel() }}
    </div>

    @if (editMode()) {
      <div
        class="absolute inset-0 flex items-center justify-center rounded-[inherit]
               bg-black/50 opacity-0 group-hover:opacity-100
               transition-opacity duration-150 pointer-events-none text-white/85 text-base"
        aria-hidden="true"
      >
        <i class="fa-solid fa-paintbrush"></i>
      </div>
    }

    @if (!editMode()) {
      <div
        class="absolute bottom-full left-1/2 -translate-x-1/2 mb-2 z-20
               px-2.5 py-1.5 rounded-md text-xs whitespace-nowrap pointer-events-none
               bg-gray-800 border border-gray-700 text-gray-200
               opacity-0 group-hover:opacity-100 transition-opacity duration-150"
        role="tooltip"
      >
        <div>Posição {{ pos().posicao }}</div>
        <div class="text-gray-400">Cor: {{ colorLabel() }}</div>
      </div>
    }
  `,
  host: {
    // Célula fluida: a largura segue a coluna do grid (6 colunas fixas = planta).
    // aspect-square mantém o quadrado e faz a célula encolher no mobile — a contagem
    // de colunas NUNCA muda; só o tamanho da célula.
    'class': `
      group relative flex flex-col items-center justify-center gap-1
      w-full aspect-square min-w-0 rounded-lg border select-none
      transition-all duration-150
    `,
    '[class]': 'hostClasses()',
    'role': 'gridcell',
    '[attr.aria-label]': '"Posição " + pos().posicao + ", cor " + colorLabel()',
    '[attr.tabindex]': 'editMode() ? 0 : -1',
    '(click)': 'cellClick.emit(pos())',
    '(keydown.enter)': 'editMode() && cellClick.emit(pos())',
    '(keydown.space)': '$event.preventDefault(); editMode() && cellClick.emit(pos())',
  },
})
export class EstoqueCellComponent {
  readonly pos      = input.required<Estoque>();
  readonly editMode = input.required<boolean>();
  readonly changed  = input<boolean>(false);
  readonly cor      = input.required<CorBloco>();

  readonly cellClick = output<Estoque>();

  protected colorLabel(): string {
    return COLOR_LABEL[this.cor()];
  }

  protected hostClasses = computed((): string => {
    const color   = CELL_COLOR_CLASS[this.cor()];
    const edit    = this.editMode()
      ? 'cursor-crosshair hover:-translate-y-0.5 hover:scale-105 hover:brightness-110 hover:z-10'
      : 'cursor-default';
    const changed = this.changed() && this.editMode()
      ? 'outline outline-2 outline-dashed outline-amber-400/60 -outline-offset-2'
      : '';
    return `${color} ${edit} ${changed}`;
  });
}