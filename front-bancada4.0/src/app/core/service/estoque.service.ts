import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { Estoque } from '../models/estoque.model';
import { ConfigService } from './config.service';
import { CorBloco } from '../models/enums/corbloco.enum';
import { SseClient } from './sse-client';
import { tap } from 'rxjs';

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
 
  saveChanges(
    estoqueList: Estoque[],
    changes: Map<number, EstoqueEditChange>
  ) {
    const estoqueDTOs = Array.from(changes.values()).map(change => ({
      posicao: change.posicao,
      cor: change.newCor
    }));

    return this.http.put<Estoque[]>(
      `${this.config.apiUrl}/api/estoque`,
      estoqueDTOs
    ).pipe(
      tap((estoqueAtualizado) => {
        const atual = this.snapshot() ?? estoqueList;
        const atualizadoMap = new Map(estoqueAtualizado.map(e => [e.posicao, e]));
        this.snapshot.set(atual.map(e => atualizadoMap.get(e.posicao) ?? e));
      })
    );
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
