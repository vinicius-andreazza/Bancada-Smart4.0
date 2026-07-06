import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { Expedicao } from '../models/expedicao.model';
import { ConfigService } from './config.service';
import { SseClient } from './sse-client';

@Injectable({
  providedIn: 'root',
})
export class ExpedicaoService {

  private readonly http = inject(HttpClient);
  private readonly config = inject(ConfigService);

  private readonly sse = new SseClient();

  readonly snapshot = signal<Expedicao[] | null>(null);
  readonly connectionStatus = this.sse.status;

  getExpedicao() {
     return this.http.get<Expedicao[]>( `${this.config.apiUrl}/api/expedicao` );
  }

  connect(): void {
    this.sse.connect(`${this.config.apiUrl}/api/expedicao/read`, 'expedicao', (raw) => {
      if (Array.isArray(raw)) {
        this.snapshot.set(raw as Expedicao[]);
      }
    });
  }

  disconnect(): void {
    this.sse.disconnect();
  }
}
