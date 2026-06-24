import { Component, input } from '@angular/core';
import { Pedido } from '../../../../core/models/pedido.model';
import { CorTampa } from '../../../../core/models/enums/cortampa.enum';
import {
  TIPO_LABEL,
  TAMPA_LABEL,
  getTotalBlocos,
} from '../../../pedidos/shared/utils/pedido-labels';

const TAMPA_HEX: Record<CorTampa, string> = {
  [CorTampa.PRETO]:    '#1c1c1c',
  [CorTampa.VERMELHO]: '#c41e1e',
  [CorTampa.AZUL]:     '#1a4dcc',
};

@Component({
  selector: 'app-pedidos-producao-panel',
  template: `
    <div class="flex flex-col gap-2">

      <div class="flex items-center gap-1.5">
        <i class="fa-solid fa-list-check text-blue-400 text-[0.65rem]" aria-hidden="true"></i>
        <span class="text-[0.6rem] font-semibold uppercase tracking-widest text-gray-400">Pedidos em Produção</span>
        @if (!carregando() && !erro()) {
          <span class="ml-auto text-[0.6rem] font-mono text-gray-600">{{ pedidos().length }}</span>
        }
      </div>

      @if (carregando()) {
        <p class="text-[0.65rem] text-gray-500 font-mono">Carregando...</p>
      } @else if (erro()) {
        <p class="text-[0.65rem] text-red-400 font-mono">Falha ao carregar pedidos.</p>
      } @else if (pedidos().length === 0) {
        <p class="text-[0.65rem] text-gray-600 font-mono italic">Nenhum pedido em produção.</p>
      } @else {
        <ul class="flex flex-col gap-1 max-h-[400px] overflow-y-auto pr-0.5">
          @for (pedido of pedidos(); track pedido.id) {
            <li class="flex flex-col gap-1 rounded-md bg-gray-950/60 border border-border-subtle px-3 py-2">
              <div class="flex items-center gap-2">
                <span class="font-mono text-sm font-semibold text-gray-200">{{ pedido.codPedido }}</span>
                <span class="text-[0.6rem] text-gray-500">{{ tipoLabel[pedido.tipoPedido] }}</span>
                <span class="ml-auto flex items-center gap-1">
                  <span
                    class="w-2.5 h-2.5 rounded-sm border border-white/10 shrink-0"
                    [style.background]="tampaHex[pedido.corTampa]"
                    aria-hidden="true"
                  ></span>
                  <span class="text-[0.6rem] text-gray-500">{{ tampaLabel[pedido.corTampa] }}</span>
                </span>
              </div>
              <div class="flex items-center justify-between">
                <span class="text-[0.6rem] font-mono text-gray-600">
                  {{ totalBlocos(pedido) }} {{ totalBlocos(pedido) === 1 ? 'bloco' : 'blocos' }}
                </span>
                <span class="text-[0.6rem] font-mono text-gray-600">
                  {{ pedido.dataEntrada ?? pedido.dataCriacao }}
                </span>
              </div>
            </li>
          }
        </ul>
      }

    </div>
  `,
})
export class PedidosProducaoPanelComponent {
  readonly pedidos    = input<Pedido[]>([]);
  readonly carregando = input(false);
  readonly erro       = input(false);

  protected readonly tipoLabel   = TIPO_LABEL;
  protected readonly tampaLabel  = TAMPA_LABEL;
  protected readonly tampaHex    = TAMPA_HEX;
  protected readonly totalBlocos = getTotalBlocos;
}
