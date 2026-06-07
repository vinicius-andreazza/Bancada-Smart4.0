import { Component, computed, input, output } from "@angular/core";
import { ButtonComponent } from "../../../../shared/components/button/button";
import { ModalComponent } from "../../../../shared/components/modal/modal";
import { StatusPedido } from "../../../../core/models/enums/statuspedido";
import { PedidoDetalheBlocoComponent } from "./pedido-detalhe-bloco/pedido-detalhe-bloco";
import { Pedido } from "../../../../core/models/pedido";
import { CorTampa } from "../../../../core/models/enums/cortampa";
import { TipoPedido } from "../../../../core/models/enums/tipopedido";

const STATUS_LABEL: Record<StatusPedido, string> = {
  [StatusPedido.PENDENTE]:  'Pendente',
  [StatusPedido.PRODUCAO]:  'Em Produção',
  [StatusPedido.CONCLUIDO]: 'Concluído',
};

const STATUS_BADGE_CLASS: Record<StatusPedido, string> = {
  [StatusPedido.PENDENTE]:  'bg-amber-500/10 text-amber-400 border border-amber-500/20',
  [StatusPedido.PRODUCAO]:  'bg-blue-500/10  text-blue-400  border border-blue-500/20',
  [StatusPedido.CONCLUIDO]: 'bg-green-500/10 text-green-400 border border-green-500/20',
};

const TIPO_PEDIDO_LABEL: Record<TipoPedido, string> = {
  [TipoPedido.SIMPLES]: 'Simples',
  [TipoPedido.DUPLO]:   'Duplo',
  [TipoPedido.TRIPLO]:  'Triplo',
};

const COR_TAMPA_LABEL: Record<CorTampa, string> = {
  [CorTampa.PRETO]:    'Preto',
  [CorTampa.VERMELHO]: 'Vermelho',
  [CorTampa.AZUL]:     'Azul',
};

@Component({
  selector: 'app-pedido-detalhe-modal',
  imports: [ModalComponent, ButtonComponent, PedidoDetalheBlocoComponent],
  templateUrl: './pedido-detalhe-modal.html',
})
export class PedidoDetalheModalComponent {
  readonly pedido         = input.required<Pedido>();
  readonly fechar         = output<void>();
  readonly enviarProducao = output<Pedido>();
  readonly retirar        = output<Pedido>();

  protected readonly StatusPedido = StatusPedido;

  protected readonly statusLabel      = computed(() => STATUS_LABEL[this.pedido().status]);
  protected readonly statusBadgeClass = computed(() => STATUS_BADGE_CLASS[this.pedido().status]);

  protected readonly metaInfo = computed(() => [
    { label: 'Tipo',  value: TIPO_PEDIDO_LABEL[this.pedido().tipoPedido] },
    { label: 'Tampa', value: COR_TAMPA_LABEL[this.pedido().corTampa]     },
    { label: 'Data',  value: this.pedido().dataCriacao                   },
  ]);
}