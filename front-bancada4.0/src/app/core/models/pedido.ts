import { Bloco } from "./bloco";
import { CorTampa } from "./enums/cortampa";
import { StatusPedido } from "./enums/statuspedido";
import { TipoPedido } from "./enums/tipopedido"; 

export interface Pedido {
    id: number,
    codPedido: string,
    dataCriacao: string,
    status: StatusPedido,
    tipoPedido: TipoPedido,
    corTampa: CorTampa,
    dataEntrada: string | null,
    expedicao: number,
    blocos: Bloco[]

}
