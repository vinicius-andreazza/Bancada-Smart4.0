import { signal } from '@angular/core';

export type SseConnectionStatus = 'connecting' | 'connected' | 'reconnecting' | 'error';

const BACKOFF_INICIAL_MS = 1_000;
const BACKOFF_MAXIMO_MS = 30_000;

/**
 * Encapsula um EventSource com parsing seguro do payload e reconexão.
 *
 * - Evento com JSON malformado é ignorado (não derruba o listener nem o snapshot atual).
 * - Queda de conexão com o stream ainda aberto ('reconnecting'): o próprio EventSource
 *   retenta; apenas refletimos o estado.
 * - Stream fechado pelo navegador ('error'): recriamos o EventSource com backoff
 *   exponencial limitado.
 */
export class SseClient {
  readonly status = signal<SseConnectionStatus>('connecting');

  private eventSource?: EventSource;
  private backoffMs = BACKOFF_INICIAL_MS;
  private reconexaoTimer?: ReturnType<typeof setTimeout>;

  connect(url: string, eventName: string, onData: (raw: unknown) => void): void {
    this.limparReconexao();
    this.eventSource?.close();
    this.status.set('connecting');

    const es = new EventSource(url);
    this.eventSource = es;

    es.onopen = () => {
      this.backoffMs = BACKOFF_INICIAL_MS;
      this.status.set('connected');
    };

    es.addEventListener(eventName, (event: MessageEvent<string>) => {
      try {
        onData(JSON.parse(event.data));
      } catch {
        // Payload malformado: mantém o último snapshot e segue ouvindo.
      }
    });

    es.onerror = () => {
      if (es.readyState === EventSource.CLOSED) {
        this.status.set('error');
        this.reconexaoTimer = setTimeout(() => {
          this.backoffMs = Math.min(this.backoffMs * 2, BACKOFF_MAXIMO_MS);
          this.connect(url, eventName, onData);
        }, this.backoffMs);
      } else {
        this.status.set('reconnecting');
      }
    };
  }

  disconnect(): void {
    this.limparReconexao();
    this.eventSource?.close();
    this.eventSource = undefined;
    this.status.set('connecting');
  }

  private limparReconexao(): void {
    if (this.reconexaoTimer !== undefined) {
      clearTimeout(this.reconexaoTimer);
      this.reconexaoTimer = undefined;
    }
  }
}
