import { Component, input, output, computed } from '@angular/core';
import { Pedido } from '../../../../core/models/pedido.model';
import { CorTampa } from '../../../../core/models/enums/cortampa.enum';
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
  selector: 'tr[app-pedido-row]',
  imports: [ButtonComponent],
  templateUrl: './pedido-row.component.html',
})
export class PedidoRow {
  readonly pedido = input.required<Pedido>();

  readonly detalheClick = output<Pedido>();
  readonly excluirClick = output<number>();

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

  verDetalhe(): void {
    this.detalheClick.emit(this.pedido());
  }

  excluir(): void {
    this.excluirClick.emit(this.pedido().id);
  }
}
