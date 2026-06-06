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

  postPedido(pedido: Pedido){
    console.log(pedido)
    return this.http.post( `${this.config.apiUrl}/api/pedidos`, pedido); 
  }
}
