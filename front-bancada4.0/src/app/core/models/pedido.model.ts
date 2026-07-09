import { Bloco } from "./bloco.model";
import { CorTampa } from "./enums/cortampa.enum";
import { StatusPedido } from "./enums/statuspedido.enum";
import { TipoPedido } from "./enums/tipopedido.enum"; 

export interface Pedido {
    id: number,
    codPedido: number,
    dataCriacao: string,
    status: StatusPedido,
    tipoPedido: TipoPedido,
    corTampa: CorTampa | null,
    dataInicio: string | null,
    dataEntrada: string | null,
    idExpedicao: number,
    blocos: Bloco[]

}
