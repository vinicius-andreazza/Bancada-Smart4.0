import { Lamina } from "./lamina";

export interface Bloco {
    id: number,
    vl_cor: number,
    posEstoque: number,
    andar: number,
    laminas: Lamina
}
