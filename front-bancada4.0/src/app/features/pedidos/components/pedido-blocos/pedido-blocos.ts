import { Component, input } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormArray, FormControl } from '@angular/forms';
import { ButtonComponent } from '../../../../shared/components/button/button';
import { PedidoLaminas } from '../pedido-laminas/pedido-laminas';

@Component({
  selector: 'app-pedido-blocos',
  imports: [ReactiveFormsModule, ButtonComponent, PedidoLaminas],
  templateUrl: './pedido-blocos.html',
})
export class PedidoBlocos {
  andar = input.required<number>();
  form = input.required<FormGroup>();

  get laminas(): FormArray {
    return this.form().get('laminas') as FormArray;
  }

  addLamina(): void {
    this.laminas.push(new FormGroup({
      corLamina:     new FormControl('1'),
      padraoLamina:  new FormControl('0'),
      posicaoLamina: new FormControl('1'),
    }));
  }

  removerLamina(): void {
    this.laminas.removeAt(this.laminas.length - 1);
  }
}