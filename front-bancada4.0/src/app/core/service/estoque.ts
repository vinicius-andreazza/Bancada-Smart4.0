import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Estoque } from '../models/estoque';
import { ConfigService } from './config';

@Injectable({
  providedIn: 'root',
})
export class EstoqueService {
  private readonly http = inject(HttpClient); 
  private readonly config = inject(ConfigService);
  getEstoque() {
     return this.http.get<Estoque[]>( `${this.config.apiUrl}/api/estoque` ); 
  }

}
