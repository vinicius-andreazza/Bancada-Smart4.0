import { NgClass } from '@angular/common';
import { Component, computed, input } from '@angular/core';
import { Estoque } from '../../../../core/models/estoque.model';
import { CorBloco } from '../../../../core/models/enums/corbloco.enum';

@Component({
  selector: 'app-estoque-panel-clp',
  imports: [NgClass],
  template: `
    <div class="flex flex-col gap-2">

      <div class="flex items-center gap-1.5">
        <i class="fa-solid fa-boxes-stacked text-indigo-400 text-[0.65rem]" aria-hidden="true"></i>
        <span class="text-[0.6rem] font-semibold uppercase tracking-widest text-gray-400">Estoque</span>
      </div>

      <div class="grid grid-cols-6 gap-1" role="grid" aria-label="Grade de estoque do CLP">
        @for (slot of slots(); track slot.posicao) {
          <div
            class="w-9 h-9 rounded border flex items-end justify-start p-0.5 transition-colors duration-150"
            [ngClass]="corClasses[slot.cor]"
            role="gridcell"
            [attr.aria-label]="'Posição ' + slot.posicao"
          >
            <span class="text-[8px] font-mono leading-none opacity-50 select-none">{{ slot.posicao }}</span>
          </div>
        }
      </div>

    </div>
  `,
})
export class EstoquePanelClpComponent {
  readonly positions = input<Estoque[]>([]);

  private readonly TOTAL = 28;

  protected readonly corClasses: Record<CorBloco, string> = {
    [CorBloco.VAZIO]:    'bg-block-vazio-bg border-block-vazio-border text-block-vazio-text',
    [CorBloco.PRETO]:    'bg-block-preto-bg border-block-preto-border text-block-preto-text',
    [CorBloco.VERMELHO]: 'bg-block-vermelho-bg border-block-vermelho-border text-block-vermelho-text',
    [CorBloco.AZUL]:     'bg-block-azul-bg border-block-azul-border text-block-azul-text',
  };

  readonly slots = computed(() => {
    const data = this.positions();
    return Array.from({ length: this.TOTAL }, (_, i) => {
      const posicao = i + 1;
      return data.find(e => e.posicao === posicao) ?? { posicao, cor: CorBloco.VAZIO };
    });
  });
}
