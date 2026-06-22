import { inject, Injectable, signal } from '@angular/core';
import { ConfigService } from './config.service';
import { MonitoramentoSnapshot } from '../models/monitoramento.model';
import { Estacao } from '../models/enums/estacao.enum';
import { StatusEstacao } from '../models/enums/statusestacao.enum';

export type MonitoramentoConnectionStatus = 'connecting' | 'connected' | 'error';

@Injectable({
  providedIn: 'root',
})
export class MonitoramentoService {
  private readonly config = inject(ConfigService);

  private eventSource?: EventSource;

  readonly snapshot = signal<MonitoramentoSnapshot | null>(null);
  readonly connectionStatus = signal<MonitoramentoConnectionStatus>('connecting');

  connect(): void {
    this.eventSource = new EventSource(`${this.config.apiUrl}/api/smart/readAll`);

    this.eventSource.onopen = () => {
      this.connectionStatus.set('connected');
    };

   this.eventSource.addEventListener('monitoramento', (event: MessageEvent<string>) => {
    console.log(event.data)
      const raw = JSON.parse(event.data);
      this.snapshot.set({
        codPedidoAtual: raw.codPedidoAtual ?? null,
        duracao:   raw.duracao ?? null,
        estacoes: (raw.estacaoStatus ?? []).map((e: { estacao: string; status: string; atualizadoEm: string }) => ({
          estacao:      e.estacao.toLowerCase() as Estacao,
          status:       e.status.toLowerCase()  as StatusEstacao,
          atualizadoEm: e.atualizadoEm,
        })),
        estoque:        raw.estoque ?? [],       
        expedicao:      raw.expedicao ?? [],      
      } as MonitoramentoSnapshot);
    });

    this.eventSource.onerror = () => {
      this.connectionStatus.set('error');
    };
  }

  disconnect(): void {
    this.eventSource?.close();
    this.eventSource = undefined;
  }
}
