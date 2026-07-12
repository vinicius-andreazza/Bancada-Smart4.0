import { Pedido } from '../../../../core/models/pedido.model';
import { StatusPedido } from '../../../../core/models/enums/statuspedido.enum';
import { TipoPedido } from '../../../../core/models/enums/tipopedido.enum';
import { CorTampa } from '../../../../core/models/enums/cortampa.enum';


export const STATUS_LABEL: Record<StatusPedido, string> = {
  [StatusPedido.PENDENTE]:  'Pendente',
  [StatusPedido.PRODUCAO]:  'Em Produção',
  [StatusPedido.CONCLUIDO]: 'Concluído',
  [StatusPedido.CANCELADO]: 'Cancelado',
};

export const BADGE_BASE =
  'inline-flex items-center gap-1.5 rounded-full border px-2.5 py-0.5 text-xs font-medium whitespace-nowrap';

export const STATUS_BADGE_CLASS: Record<StatusPedido, string> = {
  [StatusPedido.PENDENTE]:  `${BADGE_BASE} bg-amber-500/10 text-amber-400 border-amber-500/20`,
  [StatusPedido.PRODUCAO]:  `${BADGE_BASE} bg-blue-500/10  text-blue-400  border-blue-500/20`,
  [StatusPedido.CONCLUIDO]: `${BADGE_BASE} bg-emerald-500/10 text-emerald-400 border-emerald-500/20`,
  [StatusPedido.CANCELADO]: `${BADGE_BASE} bg-red-500/10   text-red-400   border-red-500/20`,
};

export const STATUS_ICON: Record<StatusPedido, string> = {
  [StatusPedido.PENDENTE]:  'fa-solid fa-clock',
  [StatusPedido.PRODUCAO]:  'fa-solid fa-gears',
  [StatusPedido.CONCLUIDO]: 'fa-solid fa-circle-check',
  [StatusPedido.CANCELADO]: 'fa-solid fa-ban',
};

export const TIPO_LABEL: Record<TipoPedido, string> = {
  [TipoPedido.SIMPLES]: 'Simples',
  [TipoPedido.DUPLO]:   'Duplo',
  [TipoPedido.TRIPLO]:  'Triplo',
};


export const TIPO_BADGE_CLASS: Record<TipoPedido, string> = {
  [TipoPedido.SIMPLES]: `${BADGE_BASE} bg-slate-500/10  text-slate-300  border-slate-500/20`,
  [TipoPedido.DUPLO]:   `${BADGE_BASE} bg-indigo-500/10 text-indigo-400 border-indigo-500/20`,
  [TipoPedido.TRIPLO]:  `${BADGE_BASE} bg-purple-500/10 text-purple-400 border-purple-500/20`,
};

export const TAMPA_LABEL: Record<CorTampa, string> = {
  [CorTampa.PRETO]:    'Preto',
  [CorTampa.VERMELHO]: 'Vermelho',
  [CorTampa.AZUL]:     'Azul',
};

export function tampaLabelNullable(cor: CorTampa | null): string {
  return cor !== null ? TAMPA_LABEL[cor] : 'Sem tampa';
}

export const TAMPA_CSS_CLASS: Record<CorTampa, string> = {
  [CorTampa.PRETO]:    'color-swatch sw-preto',
  [CorTampa.VERMELHO]: 'color-swatch sw-vermelho',
  [CorTampa.AZUL]:     'color-swatch sw-azul',
};

export function getTotalBlocos(pedido: Pedido): number {
  const b = pedido.blocos;
  return Array.isArray(b) ? b.length : b ? 1 : 0;
}
