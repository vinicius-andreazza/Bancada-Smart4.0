import { Component, computed, input } from '@angular/core';
import { Expedicao } from '../../../../core/models/expedicao.model';

@Component({
  selector: 'app-expedicao-panel-clp',
  template: `
    <div class="flex flex-col gap-2">

      <div class="flex items-center gap-1.5">
        <i class="fa-solid fa-truck-fast text-emerald-400 text-[0.65rem]" aria-hidden="true"></i>
        <span class="text-[0.6rem] font-semibold uppercase tracking-widest text-gray-400">Expedição</span>
      </div>

      <div class="grid grid-cols-4 gap-1 w-[196px]">
        @for (slot of slots(); track slot.posicao) {
          <div
            class="aspect-square rounded border flex flex-col items-center justify-center gap-0.5 transition-colors duration-150"
            [class]="slot.codPedido
              ? 'bg-amber-950/40 border-amber-500/30'
              : 'bg-block-vazio-bg border-block-vazio-border'"
          >
            <span class="text-[7px] font-mono text-gray-500 leading-none select-none">P{{ slot.posicao }}</span>
            <span
              class="text-[10px] font-bold font-mono leading-none"
              [class]="slot.codPedido ? 'text-amber-300' : 'text-block-vazio-text'"
            >
              {{ slot.codPedido ?? '—' }}
            </span>
          </div>
        }
      </div>

    </div>
  `,
})
export class ExpedicaoPanelClpComponent {
  readonly positions = input<Expedicao[]>([]);

  private readonly TOTAL = 12;

  readonly slots = computed(() => {
    const data = this.positions();
    return Array.from({ length: this.TOTAL }, (_, i) => {
      const posicao = i + 1;
      const found = data.find(e => e.posicao === posicao);
      return {
        posicao,
        codPedido: found?.pedido?.codPedido ?? null,
      };
    });
  });
}
