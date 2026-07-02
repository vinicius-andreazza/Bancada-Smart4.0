import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { Estoque } from '../models/estoque.model';
import { ConfigService } from './config.service';
import { CorBloco } from '../models/enums/corbloco.enum';

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
  
    private eventSource?: EventSource;
  
    readonly snapshot = signal<Estoque[] | null>(null);
  
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
      this.eventSource = new EventSource(`${this.config.apiUrl}/api/estoque/read`);
  
      this.eventSource.onopen = () => {
        //
      };
  
     this.eventSource.addEventListener('estoque', (event: MessageEvent<string>) => {
      console.log(event.data)
        const raw = JSON.parse(event.data);
        this.snapshot.set(raw as Estoque[]);
      });
  
      this.eventSource.onerror = () => {
        //
      };
    }
  
    disconnect(): void {
      this.eventSource?.close();
      this.eventSource = undefined;
    }

}
