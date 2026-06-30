import { AndarBloco } from "./enums/andarbloco.enum";
import { CorBloco } from "./enums/corbloco.enum";
import { Lamina } from "./lamina.model";

export interface Bloco {
    id: number,
    corBloco: CorBloco,
    andar: AndarBloco,
    laminas: Lamina[]
}
