import { AndarBloco } from "./enums/andarbloco";
import { CorBloco } from "./enums/corbloco";
import { Lamina } from "./lamina";

export interface Bloco {
    id: number,
    corBloco: CorBloco,
    posEstoque: number,
    andar: AndarBloco,
    laminas: Lamina
}
