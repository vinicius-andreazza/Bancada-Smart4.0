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
  readonly modoLeitura = signal(false);

  readonly bancadaConfig = signal<BancadaConfig | null>(this.lerStorage());

  connect(bancada: BancadaConfig): Observable<unknown> {
    const body = {
      estoqueIp: bancada.estoqueIp,
      processoIp: bancada.processoIp,
      montagemIp: bancada.montagemIp,
      expedicaoIp: bancada.expedicaoIp,
      endpointSeletorTampa: bancada.endpointSeletorTampa
        ? bancada.endpointSeletorTampa
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

  desconectar(): Observable<unknown> {
    return this.http.post(`${this.config.apiUrl}/api/smart/close`, null).pipe(
      tap(() => this.desconectarLocal()),
    );
  }

  desconectarLocal(): void {
    this.bancadaConfig.set(null);
    this.limparStorage();
    this.isConnected.set(false);
    this.modoLeitura.set(false);
  }

  ativarModoLeitura(): Observable<boolean> {
    return this.http.post<boolean>(`${this.config.apiUrl}/api/smart/readMode`, { ativo: true }).pipe(
      tap((ativo) => this.modoLeitura.set(ativo)),
    );
  }

  desativarModoLeitura(): Observable<boolean> {
    return this.http.post<boolean>(`${this.config.apiUrl}/api/smart/readMode`, { ativo: false }).pipe(
      tap((ativo) => this.modoLeitura.set(ativo)),
    );
  }

  getModoLeitura(): Observable<boolean> {
    return this.http.get<boolean>(`${this.config.apiUrl}/api/smart/readMode`);
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

  private limparStorage(): void {
    try {
      localStorage.removeItem(STORAGE_KEY);
    } catch {
      // storage indisponível
    }
  }
}
