import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { Estoque } from '../models/estoque.model';
import { ConfigService } from './config.service';
import { CorBloco } from '../models/enums/corbloco.enum';
import { SseClient } from './sse-client';

export interface EstoqueEditChange {
  posicao: number;
  originalCor: CorBloco;
  newCor: CorBloco;
}

@Injectable({
  providedIn: 'root',
})
export class EstoqueService {
  private readonly http = inject(HttpClient);
  private readonly config = inject(ConfigService);
  private readonly sse = new SseClient();

  readonly snapshot = signal<Estoque[] | null>(null);
  readonly connectionStatus = this.sse.status;
  
  getEstoque() {
    return this.http.get<Estoque[]>( `${this.config.apiUrl}/api/estoque`); 
  }
 
  saveChanges(estoqueList: Estoque[], changes:  Map<number, EstoqueEditChange>) {
    const requests = Array.from(changes.values()).map((change) => {
      const item = estoqueList.find((e) => e.posicao === change.posicao);
      if (!item) return null;
      return this.http.put(`${this.config.apiUrl}/api/estoque/${item.posicao}`, {
        cor: change.newCor,
      });
    }).filter(Boolean);
 
    return requests;
  }

  connect(): void {
    this.sse.connect(`${this.config.apiUrl}/api/estoque/read`, 'estoque', (raw) => {
      if (Array.isArray(raw)) {
        this.snapshot.set(raw as Estoque[]);
      }
    });
  }

  disconnect(): void {
    this.sse.disconnect();
  }

}
