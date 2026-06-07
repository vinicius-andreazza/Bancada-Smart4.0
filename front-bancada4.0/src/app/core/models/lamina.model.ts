import { CorLamina } from "./enums/corlamina.enum";
import { PadraoLamina } from "./enums/padraolamina.enum";
import { PosicaoLamina } from "./enums/posicaolamina.enum";

export interface Lamina {
    id: number,
    corLamina: CorLamina,
    padraoLamina: PadraoLamina,
    posicaoLamina: PosicaoLamina
}
