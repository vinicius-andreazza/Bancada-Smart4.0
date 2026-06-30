import { Component, input, output } from '@angular/core';
import { Pedido } from '../../../../core/models/pedido.model';
import { CorTampa } from '../../../../core/models/enums/cortampa.enum';
import { ButtonComponent } from '../../../../shared/components/button/button.component';
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
  imports: [ButtonComponent],
  template: `
    <div class="flex flex-col gap-2">

      <div class="flex items-center gap-1.5">
        <i class="fa-solid fa-list-check text-accent text-[0.65rem]" aria-hidden="true"></i>
        <span class="text-[0.6rem] font-semibold uppercase tracking-widest text-muted">Pedidos em Produção</span>
        @if (!carregando() && !erro()) {
          <span class="ml-auto text-[0.6rem] font-mono text-muted">{{ pedidos().length }}</span>
        }
      </div>

      @if (carregando()) {
        <p class="text-[0.65rem] text-muted font-mono">Carregando...</p>
      } @else if (erro()) {
        <p class="text-[0.65rem] text-red-400 font-mono">Falha ao carregar pedidos.</p>
      } @else if (pedidos().length === 0) {
        <p class="text-[0.65rem] text-muted font-mono italic">Nenhum pedido em produção.</p>
      } @else {
        <ul class="flex flex-col gap-1.5 max-h-[400px] overflow-y-auto pr-0.5">
          @for (pedido of pedidos(); track pedido.id) {
            <li class="rounded-md bg-panel border border-rule px-3 py-2">

              <div class="flex items-start justify-between gap-2">
                <div class="flex flex-col gap-1 min-w-0">
                  <span class="font-mono text-sm font-bold text-bright leading-none">
                    {{ pedido.codPedido }}
                  </span>
                  <div class="flex items-center gap-2 flex-wrap">
                    <span class="text-[0.6rem] text-muted">{{ tipoLabel[pedido.tipoPedido] }}</span>
                    <span class="flex items-center gap-1">
                      <span
                        class="w-2.5 h-2.5 rounded-sm border border-white/10 shrink-0"
                        [style.background]="tampaHex[pedido.corTampa]"
                        aria-hidden="true"
                      ></span>
                      <span class="text-[0.6rem] text-muted">{{ tampaLabel[pedido.corTampa] }}</span>
                    </span>
                  </div>
                </div>

                <app-button
                  variant="danger"
                  size="sm"
                  [loading]="removendoId() === pedido.id"
                  (click)="remover.emit(pedido.id)"
                  title="Remover da fila"
                >
                  <i class="fa-solid fa-xmark" aria-hidden="true"></i>
                </app-button>
              </div>

              <div class="flex items-center justify-between mt-1.5">
                <span class="text-[0.6rem] font-mono text-muted">
                  {{ totalBlocos(pedido) }} {{ totalBlocos(pedido) === 1 ? 'bloco' : 'blocos' }}
                </span>
                <span class="text-[0.6rem] font-mono text-muted">
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
  readonly pedidos      = input<Pedido[]>([]);
  readonly carregando   = input(false);
  readonly erro         = input(false);
  readonly removendoId  = input<number | null>(null);

  readonly remover = output<number>();

  protected readonly tipoLabel   = TIPO_LABEL;
  protected readonly tampaLabel  = TAMPA_LABEL;
  protected readonly tampaHex    = TAMPA_HEX;
  protected readonly totalBlocos = getTotalBlocos;
}
