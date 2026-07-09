import { Component, computed, input, output } from '@angular/core';
import { ModalComponent } from '../../../../shared/components/modal/modal.component';
import { Pedido } from '../../../../core/models/pedido.model';
import { PedidoDetalheBlocoComponent } from '../../../pedidos/components/pedido-detalhe-modal/pedido-detalhe-bloco/pedido-detalhe-bloco.component';
import { Pedido3dPreviewComponent } from '../../../pedidos/components/pedido-3d-preview/pedido-3d-preview.component';
import { STATUS_LABEL, STATUS_BADGE_CLASS, TIPO_LABEL, tampaLabelNullable } from '../../../pedidos/shared/utils/pedido-labels';
import {
  PedidoPreviewConfig,
  BlocoConfig,
  LaminaConfig,
} from '../../../pedidos/components/pedido-3d-preview/bloco-3d-viewer.component';

@Component({
  selector: 'app-ultimo-pedido-modal',
  imports: [ModalComponent, PedidoDetalheBlocoComponent, Pedido3dPreviewComponent],
  template: `
    <app-modal
      [labelledBy]="'ultimo-pedido-titulo'"
      maxWidth="4xl"
      [hasFooter]="false"
      (fechar)="fechar.emit()"
    >
      <h2 slot="header" id="ultimo-pedido-titulo" class="text-lg font-heading font-bold text-text-primary">
        Último Pedido Concluído
      </h2>

      <div slot="body">
        @if (loading()) {
          <p class="text-text-secondary text-sm p-6">Carregando...</p>
        } @else if (error() || !resumo()) {
          <p class="text-text-secondary text-sm p-6">Nenhum pedido concluído encontrado.</p>
        } @else {
          <div class="grid grid-cols-1 lg:grid-cols-[minmax(0,1fr)_320px] xl:grid-cols-[minmax(0,1fr)_360px] items-start">

            <div class="flex flex-col min-w-0 border-r border-gray-800">
              <div class="flex items-center gap-2.5 px-6 py-3 border-b border-gray-800">
                <span class="text-base font-semibold text-gray-100">Pedido #{{ resumo()!.id }}</span>
                <span [class]="statusBadgeClass() + ' text-xs font-medium px-2 py-0.5 rounded-full'">
                  {{ statusLabel() }}
                </span>
                <span class="ml-auto text-xs font-mono text-gray-500">{{ resumo()!.codPedido }}</span>
              </div>

              <div class="grid grid-cols-1 sm:grid-cols-3 gap-px bg-gray-800 border-b border-gray-800">
                @for (meta of metaInfo(); track meta.label) {
                  <div class="flex flex-col gap-0.5 px-5 py-3 bg-gray-950">
                    <span class="text-xs text-gray-600 uppercase tracking-widest font-mono">{{ meta.label }}</span>
                    <span class="text-sm text-gray-200">{{ meta.value }}</span>
                  </div>
                }
              </div>

              <div class="px-6 py-4 flex flex-col gap-3">
                <span class="text-xs font-mono font-semibold tracking-widest uppercase text-gray-600">
                  Blocos ({{ resumo()!.blocos.length }})
                </span>
                @for (bloco of resumo()!.blocos; track bloco.andar) {
                  <app-pedido-detalhe-bloco [bloco]="bloco" />
                }
              </div>
            </div>

            <div class="px-4 py-4 sticky top-4">
              <app-pedido-3d-preview [staticConfig]="pedidoPreviewConfig()" />
            </div>

          </div>
        }
      </div>
    </app-modal>
  `,
})
export class UltimoPedidoModalComponent {
  readonly resumo  = input<Pedido | null>(null);
  readonly loading = input(false);
  readonly error   = input(false);

  readonly fechar = output<void>();

  readonly statusLabel = computed(() => {
    const p = this.resumo();
    return p ? STATUS_LABEL[p.status] : '';
  });
  readonly statusBadgeClass = computed(() => {
    const p = this.resumo();
    return p ? STATUS_BADGE_CLASS[p.status] : '';
  });

  readonly metaInfo = computed(() => {
    const pedido = this.resumo();
    if (!pedido) return [];
    return [
      { label: 'Tipo',  value: TIPO_LABEL[pedido.tipoPedido] },
      { label: 'Tampa', value: tampaLabelNullable(pedido.corTampa) },
      { label: 'Data',  value: pedido.dataCriacao            },
    ];
  });

  readonly pedidoPreviewConfig = computed<PedidoPreviewConfig>(() => {
    const pedido = this.resumo();
    if (!pedido) return { blocos: [], corTampa: null };

    const blocos: BlocoConfig[] = pedido.blocos.map(b => ({
      andar:    b.andar,
      corBloco: b.corBloco,
      laminas:  b.laminas.map((l): LaminaConfig => ({
        corLamina:     l.corLamina,
        padraoLamina:  l.padraoLamina,
        posicaoLamina: l.posicaoLamina,
      })),
    }));

    return {
      blocos,
      corTampa: pedido.corTampa,
    };
  });
}
