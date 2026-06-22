import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ConfigService } from './config.service';
import { Pedido } from '../models/pedido.model';
import { Page } from '../models/page.model';
import { PedidoContagens } from '../models/pedido-contagens.model';

export type PedidoFiltro = 'TODOS' | 'PENDENTE' | 'PRODUCAO' | 'CONCLUIDO' | 'CANCELADO';

const SUFIXO_ROTA: Record<PedidoFiltro, string> = {
  TODOS: '',
  PENDENTE: '/pendente',
  PRODUCAO: '/producao',
  CONCLUIDO: '/concluido',
  CANCELADO: '/cancelado',
};

@Injectable({
  providedIn: 'root',
})
export class PedidoService {
  private readonly http = inject(HttpClient);
  private readonly config = inject(ConfigService);

  getPedidosPaginado(filtro: PedidoFiltro, page: number, size: number): Observable<Page<Pedido>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Pedido>>(
      `${this.config.apiUrl}/api/pedidos${SUFIXO_ROTA[filtro]}`,
      { params },
    );
  }

  getContagens(): Observable<PedidoContagens> {
    return this.http.get<PedidoContagens>(`${this.config.apiUrl}/api/pedidos/contagens`);
  }

  postPedido(pedido: Pedido): Observable<Pedido> {
    return this.http.post<Pedido>( `${this.config.apiUrl}/api/pedidos`, pedido);
  }

  postEnviarPedido(pedido: Pedido){
    return this.http.post( `${this.config.apiUrl}/api/pedidos/enviar`, pedido);
  }

  patchPedido(id: number, pedido: Pedido){
    return this.http.patch( `${this.config.apiUrl}/api/pedidos/${id}`, pedido);
  }

  reiniciarPedido(id: number): Observable<Pedido> {
    return this.http.post<Pedido>(`${this.config.apiUrl}/api/pedidos/${id}/reiniciar`, {});
  }

  getUltimoPedido(): Observable<Pedido> {
    return this.http.get<Pedido>(`${this.config.apiUrl}/api/pedidos/ultimo`);
  }
}
