import { Component, computed, input } from '@angular/core';
import { CorBloco } from '../../../../../core/models/enums/corbloco';
import { CorLamina } from '../../../../../core/models/enums/corlamina';
import { PadraoLamina } from '../../../../../core/models/enums/padraolamina';
import { PosicaoLamina } from '../../../../../core/models/enums/posicaolamina';
import { Bloco } from '../../../../../core/models/bloco';

const COR_BLOCO_LABEL: Record<CorBloco, string> = {
  [CorBloco.VAZIO]:    '—',
  [CorBloco.PRETO]:    'Preto',
  [CorBloco.VERMELHO]: 'Vermelho',
  [CorBloco.AZUL]:     'Azul',
};

const COR_LAMINA_LABEL: Record<CorLamina, string> = {
  [CorLamina.VERMELHO]: 'Vermelho',
  [CorLamina.AZUL]:     'Azul',
  [CorLamina.AMARELO]:  'Amarelo',
  [CorLamina.VERDE]:    'Verde',
  [CorLamina.PRETO]:    'Preto',
  [CorLamina.BRANCO]:   'Branco',
};

const COR_LAMINA_HEX: Record<CorLamina, string> = {
  [CorLamina.VERMELHO]: '#ef4444',
  [CorLamina.AZUL]:     '#3b82f6',
  [CorLamina.AMARELO]:  '#eab308',
  [CorLamina.VERDE]:    '#22c55e',
  [CorLamina.PRETO]:    '#111827',
  [CorLamina.BRANCO]:   '#f9fafb',
};

const PADRAO_LAMINA_LABEL: Record<PadraoLamina, string> = {
  [PadraoLamina.NENHUM]:  'Nenhum',
  [PadraoLamina.CASA]:    'Casa',
  [PadraoLamina.NAVIO]:   'Navio',
  [PadraoLamina.ESTRELA]: 'Estrela',
};

const POSICAO_LAMINA_LABEL: Record<PosicaoLamina, string> = {
  [PosicaoLamina.ESQUERDA]: 'Esquerda',
  [PosicaoLamina.FRENTE]:   'Frente',
  [PosicaoLamina.DIREITA]:  'Direita',
};

@Component({
  selector: 'app-pedido-detalhe-bloco',
  imports: [],
  template: `
    <div class="rounded-lg border border-gray-800 bg-gray-900/50 overflow-hidden">

      <div class="flex items-center justify-between px-4 py-2.5 border-b border-gray-800 bg-gray-900">
        <span class="text-xs font-mono font-semibold tracking-widest uppercase text-gray-500">
          Andar {{ bloco().andar }}
        </span>
        <span class="text-xs text-gray-400">{{ corBlocoLabel() }}</span>
      </div>

      <div class="px-4 py-3 flex flex-col gap-3">

        <div class="flex items-center gap-2 text-xs text-gray-500">
          <i class="fa-solid fa-box-archive" aria-hidden="true"></i>
          <span>Posição estoque:</span>
          <span class="font-mono text-gray-300">{{ bloco().posEstoque || '—' }}</span>
        </div>

        @if (bloco().laminas.length > 0) {
          <div class="flex flex-col gap-2">
            <span class="text-xs font-mono font-semibold tracking-widest uppercase text-gray-600">
              Lâminas
            </span>
            <div class="flex flex-col gap-1.5">
              @for (lamina of bloco().laminas; track $index) {
                <div class="flex items-center gap-3 rounded-md bg-gray-800/50 px-3 py-2 text-xs text-gray-400">
                  <span
                    class="w-2 h-2 rounded-full flex-shrink-0"
                    [style.background]="corHex(lamina.corLamina)"
                  ></span>
                  <span class="text-gray-300">{{ corLabel(lamina.corLamina) }}</span>
                  <span class="text-gray-600">·</span>
                  <span>{{ padraoLabel(lamina.padraoLamina) }}</span>
                  <span class="text-gray-600">·</span>
                  <span>{{ posicaoLabel(lamina.posicaoLamina) }}</span>
                </div>
              }
            </div>
          </div>
        } @else {
          <p class="text-xs text-gray-600 italic">Sem lâminas cadastradas</p>
        }

      </div>
    </div>
  `,
})
export class PedidoDetalheBlocoComponent {
  readonly bloco = input.required<Bloco>();

  protected readonly corBlocoLabel = computed(() => COR_BLOCO_LABEL[this.bloco().corBloco]);

  protected corHex(cor: CorLamina):      string { return COR_LAMINA_HEX[cor]; }
  protected corLabel(cor: CorLamina):    string { return COR_LAMINA_LABEL[cor]; }
  protected padraoLabel(v: PadraoLamina): string { return PADRAO_LAMINA_LABEL[v]; }
  protected posicaoLabel(v: PosicaoLamina): string { return POSICAO_LAMINA_LABEL[v]; }
}