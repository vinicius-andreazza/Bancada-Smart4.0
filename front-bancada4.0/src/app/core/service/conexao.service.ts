import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable, firstValueFrom, tap } from 'rxjs';
import { ConfigService } from './config.service';
import { BancadaConfig } from '../models/bancada-config.model';

const STORAGE_KEY = 'bancada.config';

@Injectable({
  providedIn: 'root',
})
export class ConexaoService {
  private readonly http = inject(HttpClient);
  private readonly config = inject(ConfigService);

  readonly isConnected = signal(false);
  
  readonly bancadaConfig = signal<BancadaConfig | null>(this.lerStorage());

  connect(bancada: BancadaConfig): Observable<unknown> {
    const body = {
      estoqueIp: bancada.estoqueIp,
      processoIp: bancada.processoIp,
      montagemIp: bancada.montagemIp,
      expedicaoIp: bancada.expedicaoIp,
      endpointSeletorTampa: bancada.seletorTampasIp
        ? bancada.seletorTampasIp
        : null,
    };

    return this.http.post(`${this.config.apiUrl}/api/smart/start`, body).pipe(
      tap(() => {
        this.bancadaConfig.set(bancada);
        this.salvarStorage(bancada);
        this.isConnected.set(true);
      }),
    );
  }

  async tryReconnect(): Promise<void> {
    const salva = this.bancadaConfig();
    if (!salva) {
      return;
    }
    try {
      await firstValueFrom(this.connect(salva));
    } catch {
      this.isConnected.set(false);
    }
  }

  ativarModoTeste(): void {
    this.isConnected.set(true);
  }

  desconectar(): void {
    this.isConnected.set(false);
  }

  private lerStorage(): BancadaConfig | null {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      return raw ? (JSON.parse(raw) as BancadaConfig) : null;
    } catch {
      return null;
    }
  }

  private salvarStorage(bancada: BancadaConfig): void {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(bancada));
    } catch {
      // storage indisponível (ex.: modo privado): segue sem persistir
    }
  }
}
