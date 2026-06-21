import { Pedido } from '../../../../core/models/pedido.model';
import { StatusPedido } from '../../../../core/models/enums/statuspedido.enum';
import { TipoPedido } from '../../../../core/models/enums/tipopedido.enum';
import { CorTampa } from '../../../../core/models/enums/cortampa.enum';

/**
 * Rótulos e classes CSS compartilhados entre a linha da tabela (`pedido-row`) e o
 * card mobile (`pedido-card-item`), para evitar duplicação dos mapas.
 */
export const STATUS_LABEL: Record<StatusPedido, string> = {
  [StatusPedido.PENDENTE]:  'Pendente',
  [StatusPedido.PRODUCAO]:  'Em Produção',
  [StatusPedido.CONCLUIDO]: 'Concluído',
  [StatusPedido.CANCELADO]: 'Cancelado',
};

export const STATUS_BADGE_CLASS: Record<StatusPedido, string> = {
  [StatusPedido.PENDENTE]:  'badge-status badge-pendente',
  [StatusPedido.PRODUCAO]:  'badge-status badge-producao',
  [StatusPedido.CONCLUIDO]: 'badge-status badge-concluido',
  [StatusPedido.CANCELADO]: 'badge-status badge-cancelado',
};

export const TIPO_LABEL: Record<TipoPedido, string> = {
  [TipoPedido.SIMPLES]: 'Simples',
  [TipoPedido.DUPLO]:   'Duplo',
  [TipoPedido.TRIPLO]:  'Triplo',
};

export const TAMPA_LABEL: Record<CorTampa, string> = {
  [CorTampa.PRETO]:    'Preto',
  [CorTampa.VERMELHO]: 'Vermelho',
  [CorTampa.AZUL]:     'Azul',
};

export const TAMPA_CSS_CLASS: Record<CorTampa, string> = {
  [CorTampa.PRETO]:    'color-swatch sw-preto',
  [CorTampa.VERMELHO]: 'color-swatch sw-vermelho',
  [CorTampa.AZUL]:     'color-swatch sw-azul',
};

/** Conta os blocos de um pedido, tolerando blocos como array ou objeto único. */
export function getTotalBlocos(pedido: Pedido): number {
  const b = pedido.blocos;
  return Array.isArray(b) ? b.length : b ? 1 : 0;
}
