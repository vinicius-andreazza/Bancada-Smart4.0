import { CorLamina } from "./enums/corlamina";
import { PadraoLamina } from "./enums/padraolamina";
import { PosicaoLamina } from "./enums/posicaolamina";

export interface Lamina {
    id: number,
    corLamina: CorLamina,
    padraoLamina: PadraoLamina,
    posicaoLamina: PosicaoLamina
}
