import { Pedido } from "./pedido.model";

export interface Expedicao {
    id: number,
    posicao: number,
    pedido: Pedido | null
}
