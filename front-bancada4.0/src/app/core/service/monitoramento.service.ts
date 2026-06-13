import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { ConfigService } from './config.service';
import { MonitoramentoSnapshot, UltimoPedidoResumo } from '../models/monitoramento.model';

export type MonitoramentoConnectionStatus = 'connecting' | 'connected' | 'error';

@Injectable({
  providedIn: 'root',
})
export class MonitoramentoService {
  private readonly http = inject(HttpClient);
  private readonly config = inject(ConfigService);

  private eventSource?: EventSource;

  readonly snapshot = signal<MonitoramentoSnapshot | null>(null);
  readonly connectionStatus = signal<MonitoramentoConnectionStatus>('connecting');

  connect(): void {
    this.eventSource = new EventSource(`${this.config.apiUrl}/api/monitoramento/stream`);

    this.eventSource.onopen = () => {
      this.connectionStatus.set('connected');
    };

    this.eventSource.onmessage = (event: MessageEvent<string>) => {
      this.snapshot.set(JSON.parse(event.data) as MonitoramentoSnapshot);
    };

    this.eventSource.onerror = () => {
      this.connectionStatus.set('error');
    };
  }

  disconnect(): void {
    this.eventSource?.close();
    this.eventSource = undefined;
  }

  getUltimoPedidoResumo() {
    return this.http.get<UltimoPedidoResumo>(`${this.config.apiUrl}/api/monitoramento/ultimo-pedido`);
  }
}
