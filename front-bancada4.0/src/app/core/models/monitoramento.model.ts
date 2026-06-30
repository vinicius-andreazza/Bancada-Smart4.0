import { Estacao } from './enums/estacao.enum';
import { StatusEstacao } from './enums/statusestacao.enum';
import { Estoque } from './estoque.model';
import { Expedicao } from './expedicao.model';

export interface EstacaoStatus {
  estacao: Estacao;
  status: StatusEstacao;
  atualizadoEm: string;
}

export interface MonitoramentoSnapshot {
  codPedidoAtual: number | null;
  duracao: string | null;
  estacoes: EstacaoStatus[];
  estoque: Estoque[];
  expedicao: Expedicao[];
}
