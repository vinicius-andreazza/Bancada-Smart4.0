import { inject, Injectable, signal } from '@angular/core';
import { ConfigService } from './config.service';
import { MonitoramentoSnapshot } from '../models/monitoramento.model';

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
      const raw = JSON.parse(event.data);
      this.snapshot.set({
        codPedidoAtual: raw.codPedidoAtual ?? null,
        inicioPedido:   raw.inicioPedido ?? null,
        estacoes:       raw.estacaoStatus ?? [],
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
