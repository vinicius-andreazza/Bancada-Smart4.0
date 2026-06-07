import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { ConfigService } from './config';
import { Pedido } from '../models/pedido';

@Injectable({
  providedIn: 'root',
})
export class PedidoService {
  private readonly http = inject(HttpClient); 
  private readonly config = inject(ConfigService);

  getPedido(){
    return this.http.get<Pedido[]>( `${this.config.apiUrl}/api/pedidos`); 
  }

  postPedido(pedido: Pedido){
    return this.http.post( `${this.config.apiUrl}/api/pedidos`, pedido); 
  }
}
