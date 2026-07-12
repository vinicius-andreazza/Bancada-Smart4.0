import { inject, Injectable, signal } from '@angular/core';
import { ConfigService } from './config.service';
import { MonitoramentoSnapshot } from '../models/monitoramento.model';
import { Estacao } from '../models/enums/estacao.enum';
import { StatusEstacao } from '../models/enums/statusestacao.enum';
import { SseClient, SseConnectionStatus } from './sse-client';

export type MonitoramentoConnectionStatus = SseConnectionStatus;

interface EstacaoStatusRaw {
  estacao: string;
  status: string;
  atualizadoEm: string;
}

/** Converte o payload cru do SSE (campo `estacaoStatus`) para o modelo `MonitoramentoSnapshot`. */
export function mapMonitoramentoSnapshot(raw: unknown): MonitoramentoSnapshot {
  const dados = (raw ?? {}) as Partial<{
    codPedidoAtual: number;
    duracao: number;
    estacaoStatus: EstacaoStatusRaw[];
    estoque: MonitoramentoSnapshot['estoque'];
    expedicao: MonitoramentoSnapshot['expedicao'];
  }>;

  return {
    codPedidoAtual: dados.codPedidoAtual ?? null,
    duracao: dados.duracao ?? null,
    estacoes: (dados.estacaoStatus ?? []).map((e) => ({
      estacao: e.estacao.toLowerCase() as Estacao,
      status: e.status.toLowerCase() as StatusEstacao,
      atualizadoEm: e.atualizadoEm,
    })),
    estoque: dados.estoque ?? [],
    expedicao: dados.expedicao ?? [],
  } as MonitoramentoSnapshot;
}

@Injectable({
  providedIn: 'root',
})
export class MonitoramentoService {
  private readonly config = inject(ConfigService);
  private readonly sse = new SseClient();

  readonly snapshot = signal<MonitoramentoSnapshot | null>(null);
  readonly connectionStatus = this.sse.status;

  connect(): void {
    this.sse.connect(`${this.config.apiUrl}/api/smart/readAll`, 'monitoramento', (raw) => {
      this.snapshot.set(mapMonitoramentoSnapshot(raw));
    });
  }

  disconnect(): void {
    this.sse.disconnect();
  }
}
