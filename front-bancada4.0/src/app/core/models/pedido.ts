import { Bloco } from "./bloco";

export interface Pedido {
    id: number,
    codPedido: string,
    dataCriacao: string,
    status: number,
    tipoPedido: number,
    corTampa: number,
    dataEntrada: string | null,
    expedicao: number,
    blocos: Bloco

}
