import { Component, computed, input, output } from '@angular/core';
import { ModalComponent } from '../../../../shared/components/modal/modal.component';
import { EstacaoStatusBadgeComponent } from '../../../../shared/components/estacao-status-badge/estacao-status-badge.component';
import { UltimoPedidoResumo } from '../../../../core/models/monitoramento.model';
import { Estacao } from '../../../../core/models/enums/estacao.enum';

const ESTACAO_LABEL: Record<Estacao, string> = {
  [Estacao.ESTOQUE]: 'Estoque',
  [Estacao.PROCESSO]: 'Processo',
  [Estacao.MONTAGEM]: 'Montagem',
  [Estacao.EXPEDICAO]: 'Expedição',
};

@Component({
  selector: 'app-ultimo-pedido-modal',
  imports: [ModalComponent, EstacaoStatusBadgeComponent],
  template: `
    <app-modal
      [labelledBy]="'ultimo-pedido-titulo'"
      maxWidth="lg"
      [hasFooter]="false"
      (fechar)="fechar.emit()"
    >
      <h2 slot="header" id="ultimo-pedido-titulo" class="text-lg font-heading font-bold text-text-primary">
        Último Pedido Concluído
      </h2>

      <div slot="body" class="p-6">
        @if (loading()) {
          <p class="text-text-secondary text-sm">Carregando...</p>
        } @else if (error() || !resumo()) {
          <p class="text-text-secondary text-sm">Nenhum pedido concluído encontrado.</p>
        } @else {
          <div class="flex flex-col gap-4">
            <div class="grid grid-cols-2 gap-4">
              <div>
                <div class="text-[0.7rem] text-text-secondary font-mono tracking-[0.06em] uppercase">
                  Pedido
                </div>
                <div class="font-heading text-base font-bold text-text-primary">
                  #{{ resumo()!.codPedido }}
                </div>
              </div>
              <div>
                <div class="text-[0.7rem] text-text-secondary font-mono tracking-[0.06em] uppercase">
                  Tempo total
                </div>
                <div class="font-mono-tech text-base font-bold text-text-accent">
                  {{ tempoTotalFormatado() }}
                </div>
              </div>
              <div>
                <div class="text-[0.7rem] text-text-secondary font-mono tracking-[0.06em] uppercase">
                  Início
                </div>
                <div class="font-mono text-sm text-text-primary">
                  {{ horarioInicioFormatado() }}
                </div>
              </div>
              <div>
                <div class="text-[0.7rem] text-text-secondary font-mono tracking-[0.06em] uppercase">
                  Fim
                </div>
                <div class="font-mono text-sm text-text-primary">
                  {{ horarioFimFormatado() }}
                </div>
              </div>
            </div>

            <div>
              <div class="text-[0.7rem] text-text-secondary font-mono tracking-[0.06em] uppercase mb-2">
                Status final das estações
              </div>
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-2">
                @for (estacaoStatus of resumo()!.statusFinalEstacoes; track estacaoStatus.estacao) {
                  <div class="flex items-center justify-between gap-2">
                    <span class="text-sm text-text-primary">{{ ESTACAO_LABEL[estacaoStatus.estacao] }}</span>
                    <app-estacao-status-badge [status]="estacaoStatus.status" />
                  </div>
                }
              </div>
            </div>
          </div>
        }
      </div>
    </app-modal>
  `,
})
export class UltimoPedidoModalComponent {
  readonly resumo  = input<UltimoPedidoResumo | null>(null);
  readonly loading = input(false);
  readonly error   = input(false);

  readonly fechar = output<void>();

  protected readonly ESTACAO_LABEL = ESTACAO_LABEL;

  readonly tempoTotalFormatado = computed(() => {
    const resumo = this.resumo();
    if (!resumo) return '--:--:--';
    return this.formatDuration(resumo.tempoTotalSegundos);
  });

  readonly horarioInicioFormatado = computed(() => this.formatDateTime(this.resumo()?.horarioInicio));
  readonly horarioFimFormatado    = computed(() => this.formatDateTime(this.resumo()?.horarioFim));

  private formatDuration(totalSeconds: number): string {
    const hours = Math.floor(totalSeconds / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    const seconds = Math.floor(totalSeconds % 60);

    const pad = (value: number) => value.toString().padStart(2, '0');
    return `${pad(hours)}:${pad(minutes)}:${pad(seconds)}`;
  }

  private formatDateTime(value: string | undefined): string {
    if (!value) return '—';
    return new Date(value).toLocaleString('pt-BR');
  }
}
