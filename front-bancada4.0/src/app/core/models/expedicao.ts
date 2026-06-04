import { Pedido } from "./pedido";

export interface Expedicao {
    id: number,
    posicao: number,
    pedido: Pedido | null
}
