import { FormBuilder, FormGroup } from '@angular/forms';
import { Bloco } from '../../../core/models/bloco.model';
import { Lamina } from '../../../core/models/lamina.model';


export function criarLaminaForm(fb: FormBuilder, lamina: Lamina): FormGroup {
  return fb.group({
    id:            fb.control(lamina.id),
    corLamina:     fb.control(String(lamina.corLamina)),
    padraoLamina:  fb.control(String(lamina.padraoLamina)),
    posicaoLamina: fb.control(String(lamina.posicaoLamina)),
  });
}


export function criarBlocoForm(fb: FormBuilder, bloco: Bloco): FormGroup {
  return fb.group({
    id:       fb.control(bloco.id),
    andar:    fb.control(bloco.andar),
    corBloco: fb.control(String(bloco.corBloco)),
    laminas:  fb.array(bloco.laminas.map((lamina) => criarLaminaForm(fb, lamina))),
  });
}


export function criarBlocoVazio(fb: FormBuilder, andar: number, comId = false): FormGroup {
  return fb.group({
    ...(comId ? { id: fb.control(null) } : {}),
    andar:    fb.control(andar),
    corBloco: fb.control('1'),
    laminas:  fb.array([] as FormGroup[]),
  });
}
