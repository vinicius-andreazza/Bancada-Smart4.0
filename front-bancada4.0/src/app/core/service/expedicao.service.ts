import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { Expedicao } from '../models/expedicao.model';
import { ConfigService } from './config.service';

@Injectable({
  providedIn: 'root',
})
export class ExpedicaoService {

  private readonly http = inject(HttpClient);
  private readonly config = inject(ConfigService);

  private eventSource?: EventSource;

  readonly snapshot = signal<Expedicao[] | null>(null);

  getExpedicao() {
     return this.http.get<Expedicao[]>( `${this.config.apiUrl}/api/expedicao` );
  }

  connect(): void {
    this.eventSource = new EventSource(`${this.config.apiUrl}/api/expedicao/read`);

    this.eventSource.onopen = () => {
      console.log('[ExpedicaoService] SSE conectado');
    };

    this.eventSource.onmessage = (event) => {
      console.log('[ExpedicaoService] onmessage:', event.data);
    };

    this.eventSource.addEventListener('expedicao', (event: MessageEvent<string>) => {
      console.log('[ExpedicaoService] evento expedicao:', event.data);
      const raw = JSON.parse(event.data);
      this.snapshot.set(raw as Expedicao[]);
    });

    this.eventSource.onerror = (err) => {
      console.error('[ExpedicaoService] SSE erro:', err);
    };
  }

  disconnect(): void {
    this.eventSource?.close();
    this.eventSource = undefined;
  }
}
