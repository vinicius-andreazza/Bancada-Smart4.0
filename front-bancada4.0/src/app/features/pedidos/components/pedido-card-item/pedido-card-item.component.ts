import { Component, computed, input, output } from '@angular/core';
import { Pedido } from '../../../../core/models/pedido.model';
import { StatusPedido } from '../../../../core/models/enums/statuspedido.enum';
import { ButtonComponent } from '../../../../shared/components/button/button.component';
import {
  STATUS_LABEL,
  STATUS_BADGE_CLASS,
  STATUS_ICON,
  TIPO_LABEL,
  TIPO_BADGE_CLASS,
  TAMPA_LABEL,
  TAMPA_CSS_CLASS,
  tampaLabelNullable,
} from '../../shared/utils/pedido-labels';
import { formatarData, formatarDuracao } from '../../shared/utils/pedido-datas';

@Component({
  selector: 'app-pedido-card-item',
  imports: [ButtonComponent],
  templateUrl: './pedido-card-item.component.html',
})
export class PedidoCardItem {
  readonly pedido = input.required<Pedido>();

  readonly verDetalhe = output<Pedido>();
  readonly excluir    = output<number>();

  readonly statusLabel      = STATUS_LABEL;
  readonly statusBadgeClass = STATUS_BADGE_CLASS;
  readonly statusIcon       = STATUS_ICON;
  readonly tipoLabel        = TIPO_LABEL;
  readonly tipoBadgeClass   = TIPO_BADGE_CLASS;
  readonly tampaLabel       = TAMPA_LABEL;
  readonly tampaCssClass    = TAMPA_CSS_CLASS;

  readonly criacaoFmt   = computed(() => formatarData(this.pedido().dataCriacao));
  readonly conclusaoFmt = computed(() => formatarData(this.pedido().dataEntrada));
  readonly duracao      = computed(() =>
    formatarDuracao(this.pedido().dataInicio, this.pedido().dataEntrada),
  );
  readonly emProducao    = computed(() => this.pedido().status === StatusPedido.PRODUCAO);
  readonly corTampa      = computed(() => this.pedido().corTampa);
  readonly corTampaLabel = computed(() => tampaLabelNullable(this.pedido().corTampa));
}
