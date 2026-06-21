import { Component, input } from '@angular/core';
import { ReactiveFormsModule, FormArray, FormGroup } from '@angular/forms';
import { PedidoBlocos } from '../pedido-blocos/pedido-blocos.component';
import { SelectFieldComponent } from "../../../../shared/components/select-field/select-field.component";
import { InputFieldComponent } from "../../../../shared/components/input-field/input-field.component";

@Component({
  selector: 'app-pedido-card',
  imports: [ReactiveFormsModule, PedidoBlocos, SelectFieldComponent, InputFieldComponent],
  templateUrl: './pedido-card.component.html',
})
export class PedidoCard {
  form = input.required<FormGroup>();

  get blocos(): FormGroup[] {
    return (this.form().get('blocos') as FormArray).controls as FormGroup[];
  }

  updateType(newType: number): void {
    this.form().get('tipoPedido')?.setValue(newType);
  }
}