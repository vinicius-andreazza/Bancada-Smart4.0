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
  
  getEstoque() {
    return this.http.get<Estoque[]>( `${this.config.apiUrl}/api/estoque`); 
  }
 
  saveChanges(estoqueList: Estoque[], changes:  Map<number, EstoqueEditChange>) {
    const requests = Array.from(changes.values()).map((change) => {
      const item = estoqueList.find((e) => e.posicao === change.posicao);
      if (!item) return null;
      const id =item.posicao;
      console.log(id);
      return this.http.put(`${this.config.apiUrl}/api/estoque/${id}`, {
        cor: change.newCor,
      });
    }).filter(Boolean);
 
    return requests;
  }


}
