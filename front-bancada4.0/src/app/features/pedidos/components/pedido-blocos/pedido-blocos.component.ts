import { Component, computed, input } from '@angular/core';
import {
  ReactiveFormsModule,
  FormGroup,
  FormArray,
  FormControl,
} from '@angular/forms';
import { ButtonComponent } from '../../../../shared/components/button/button.component';
import { PedidoLaminas } from '../pedido-laminas/pedido-laminas.component';
import { SectionHeaderComponent } from '../../../../shared/components/section-header/section-header.component';
import { SelectFieldComponent } from '../../../../shared/components/select-field/select-field.component';

@Component({
  selector: 'app-pedido-blocos',
  imports: [
    ReactiveFormsModule,
    ButtonComponent,
    PedidoLaminas,
    SectionHeaderComponent,
    SelectFieldComponent
  ],
  templateUrl: './pedido-blocos.component.html',
})
export class PedidoBlocos {

  form = input.required<FormGroup>();

  corTampa = input<number | null>(null);

  get laminas(): FormArray {
    return this.form().get('laminas') as FormArray;
  }

  corTampaValue = computed(() => {
    return this.corTampa();
  });

  andarValue = computed(() => {
    return +(this.form().get('andar')?.value ?? 1);
  });

  addLamina(): void {
    this.laminas.push(
      new FormGroup({
        corLamina:     new FormControl('1'),
        padraoLamina:  new FormControl('0'),
        posicaoLamina: new FormControl(this.laminas.length+1),
      }),
    );
  }

  removerLamina(): void {
    this.laminas.removeAt(this.laminas.length - 1);
  }
}