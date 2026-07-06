import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { AppConfig } from '../models/app-config.model';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {

  private readonly http = inject(HttpClient);

  private config: AppConfig | null = null;

  /** Sinaliza que o config.json não pôde ser carregado no boot (404, JSON inválido, rede). */
  readonly loadFailed = signal(false);

  async loadConfig(): Promise<void> {
    try {
      this.config = await firstValueFrom(
        this.http.get<AppConfig>('/config/config.json')
      );
    } catch {
      this.loadFailed.set(true);
    }
  }

  get apiUrl(): string {
    return this.config?.apiUrl ?? '';
  }
}
