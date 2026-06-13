import { Estacao } from './enums/estacao.enum';
import { StatusEstacao } from './enums/statusestacao.enum';

export interface EstacaoStatus {
  estacao: Estacao;
  status: StatusEstacao;
  atualizadoEm: string;
}

export interface MonitoramentoSnapshot {
  codPedidoAtual: number | null;
  inicioPedido: string | null;
  estacoes: EstacaoStatus[];
}

export interface UltimoPedidoResumo {
  codPedido: number;
  tempoTotalSegundos: number;
  horarioInicio: string;
  horarioFim: string;
  statusFinalEstacoes: EstacaoStatus[];
}
